package com.dragon.study.springboot.autoconfigure.thrift.server;


import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

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
      Assert.isTrue(port >= 0 && port <= 65535, "port must be 0 ~ 65535");
      nonblockingServerTransport = new TNonblockingServerSocket(port);
    } catch (TTransportException e) {
      log.error(e.getMessage(), e);
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

          Assert.isNull(ifaceClass, "Multiple Thrift Ifaces defined on handler");
          ifaceClass = interfaceClass;
          processorClass = (Class<TProcessor>) innerClass;
          break;
        }
      }
      Assert.notNull(ifaceClass, "No Thrift Ifaces found on handler");
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
    Assert.notNull(transport, "no blocking server transport is null");
    args = new THsHaServer.Args(transport);

    TProtocolFactory protocolFactory = thriftProtocolFactory();
    args.protocolFactory(protocolFactory);

    TProcessor processor = null;
    try {
      processor = thriftProcessor();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    Assert.notNull(processor, "processor must not be null");
    args.processor(processor);

    args.executorService(createInvokerPool(thriftServerProperties.getMinWorker(),
        thriftServerProperties.getMaxWorker(), thriftServerProperties.getWorkerQueueCapacity()));

    return args;
  }

  private ExecutorService createInvokerPool(int minWorkerThreads, int maxWorkerThreads,
      int workerQueueCapacity) {
    int stopTimeoutVal = 60;
    TimeUnit stopTimeoutUnit = TimeUnit.SECONDS;

    LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(workerQueueCapacity);
    return new ThreadPoolExecutor(minWorkerThreads, maxWorkerThreads,
        stopTimeoutVal, stopTimeoutUnit, queue);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnExpression("#{environment.containsProperty('thrift.server.port')}")
  public TServer thriftServer() {
    String[] beanNames = applicationContext.getBeanNamesForAnnotation(ThriftService.class);
    Assert.isTrue(beanNames != null && beanNames.length != 0, "bean name is null or empty");
    THsHaServer.Args args = thriftHsHaServerArgs();
    Assert.notNull(args, "args must not null");

    log.info("thrift server is starting, service name is {}, port is {}",
        thriftServerProperties.getServiceName(), thriftServerProperties.getPort());
    return new THsHaServer(args);
  }

}