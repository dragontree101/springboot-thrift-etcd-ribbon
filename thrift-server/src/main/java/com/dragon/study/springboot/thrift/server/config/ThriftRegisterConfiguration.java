package com.dragon.study.springboot.thrift.server.config;


import com.dragon.study.springboot.etcd.EtcdAutoConfiguration;
import com.dragon.study.springboot.etcd.register.EtcdRegister;
import com.dragon.study.springboot.thrift.server.exception.ThriftServerException;
import com.dragon.study.springboot.thrift.server.utils.InetAddressUtil;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;

/**
 * Created by dragon on 16/6/3.
 */
@Configuration
@Import(EtcdAutoConfiguration.class)
@AutoConfigureAfter({ThriftAutoConfiguration.class})
@EnableConfigurationProperties({ThriftServerProperties.class})
@Slf4j
public class ThriftRegisterConfiguration {

  private final Pattern DEFAULT_PACKAGE_PATTERN = Pattern.compile(
      "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)*\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(value = "thrift.server.port", matchIfMissing = false)
  public EtcdRegister etcdRegister(EtcdClient etcdClient,
      ThriftServerProperties thriftServerProperties) {
    EtcdRegister register = new EtcdRegister();
    String serviceName = thriftServerProperties.getServiceName();

    int lastComma = serviceName.lastIndexOf(".");
    String interfaceName = serviceName.substring(0, lastComma);
    if (!DEFAULT_PACKAGE_PATTERN.matcher(interfaceName).matches()) {
      throw new ThriftServerException("interface name is not match to package pattern");
    }

    register.setPath("/dragon/service/" + interfaceName);

    String ip = InetAddressUtil.getLocalHostLANAddress().getHostAddress();

    String address = ip + ":" + String.valueOf(thriftServerProperties.getPort());
    register.setKey(address);
    register.setValue(address);

    register.setClient(etcdClient);
    register.setStart(true);

    String path = register.getPath() + "/" + register.getKey();
    log.info("path is {} register success!", path);
    return register;
  }

}
