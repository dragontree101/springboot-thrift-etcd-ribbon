package com.dragon.study.springboot.thrift.server.config;


import com.dragon.study.springboot.thrift.server.annotation.ThriftService;
import com.dragon.study.springboot.thrift.server.exception.ThriftServerException;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties({ThriftServerProperties.class})
@Slf4j
public class ThriftAutoConfiguration implements ApplicationContextAware {

  @Autowired
  private ThriftServerProperties thriftServerProperties;

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  private TProtocolFactory thriftProtocolFactory() {
    return new TBinaryProtocol.Factory(true, true);
  }

  private TNonblockingServerTransport thriftServerTransport() {
    TNonblockingServerTransport nonblockingServerTransport = null;
    try {
      int port = thriftServerProperties.getPort();
      if (port <= 0 || port >= 65535) {
        log.error("thrift server port error, port is {}", port);
        return null;
      }
      nonblockingServerTransport = new TNonblockingServerSocket(port);
    } catch (TTransportException e) {
      e.printStackTrace();
    }
    return nonblockingServerTransport;
  }

  private TProcessor thriftProcessor()
      throws ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException {
    String[] beanNames = applicationContext.getBeanNamesForAnnotation(ThriftService.class);
    if (beanNames != null) {
      Object bean = applicationContext.getBean(beanNames[0]);

      Class<?> serviceClass;

      Class<TProcessor> processorClass = null;
      Class<?> ifaceClass = null;

      Class<?>[] handlerInterfaces = ClassUtils.getAllInterfaces(bean);

      for (Class<?> interfaceClass : handlerInterfaces) {
        if (!interfaceClass.getName().endsWith("$Iface")) {
          continue;
        }

        serviceClass = interfaceClass.getDeclaringClass();
        if (serviceClass == null) {
          continue;
        }

        for (Class<?> innerClass : serviceClass.getDeclaredClasses()) {
          if (!innerClass.getName().endsWith("$Processor")) {
            continue;
          }

          if (!TProcessor.class.isAssignableFrom(innerClass)) {
            continue;
          }

          if (ifaceClass != null) {
            throw new IllegalStateException("Multiple Thrift Ifaces defined on handler");
          }

          ifaceClass = interfaceClass;
          processorClass = (Class<TProcessor>) innerClass;
          break;
        }
      }

      if (ifaceClass == null) {
        log.error("iface class is null");
        throw new IllegalStateException("No Thrift Ifaces found on handler");
      }

      Constructor<TProcessor> processorConstructor = processorClass.getConstructor(ifaceClass);
      TProcessor processor = BeanUtils.instantiateClass(processorConstructor, bean);

      thriftServerProperties.setServiceName(ifaceClass.getCanonicalName());

      return processor;
    }
    return null;
  }


  private THsHaServer.Args thriftHsHaServerArgs() {
    THsHaServer.Args args;

    TNonblockingServerTransport transport = thriftServerTransport();
    if (transport == null) {
      log.error("no blocking server transport is null");
      return null;
    }

    args = new THsHaServer.Args(transport);

    TProtocolFactory protocolFactory = thriftProtocolFactory();
    args.protocolFactory(protocolFactory);

    TProcessor processor = null;
    try {
      processor = thriftProcessor();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (processor == null) {
      log.error("processor is null");
      return null;
    }
    args.processor(processor);

    args.executorService(createInvokerPool(thriftServerProperties.getMinWorker(),
        thriftServerProperties.getMaxWorker(), thriftServerProperties.getWorkerQueueCapacity()));

    ThreadPoolExecutor executor = (ThreadPoolExecutor) args.getExecutorService();
    executor.getQueue();
    return args;
  }

  private ExecutorService createInvokerPool(int minWorkerThreads, int maxWorkerThreads,
      int workerQueueCapacity) {
    int stopTimeoutVal = 60;
    TimeUnit stopTimeoutUnit = TimeUnit.SECONDS;

    LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(workerQueueCapacity);
    ExecutorService invoker = new ThreadPoolExecutor(minWorkerThreads, maxWorkerThreads,
        stopTimeoutVal, stopTimeoutUnit, queue);

    return invoker;
  }


  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(value = "thrift.server.port", matchIfMissing = false)
  public TServer thriftServer() {
    String[] beanNames = applicationContext.getBeanNamesForAnnotation(ThriftService.class);
    if (beanNames == null || beanNames.length == 0) {
      log.error("bean name is null or empty");
      throw new ThriftServerException("no thrift service");
    }

    THsHaServer.Args args = thriftHsHaServerArgs();
    if (args == null) {
      log.error("args is null");
      throw new ThriftServerException("args is null");
    }

    log.info("thrift server is starting, service name is {}, port is {}",
        thriftServerProperties.getServiceName(), thriftServerProperties.getPort());
    return new THsHaServer(args);
  }

}