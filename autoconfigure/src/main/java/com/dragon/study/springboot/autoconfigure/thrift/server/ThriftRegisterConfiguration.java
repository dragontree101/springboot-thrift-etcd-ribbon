package com.dragon.study.springboot.autoconfigure.thrift.server;


import com.dragon.study.springboot.autoconfigure.InetAddressUtil;
import com.dragon.study.springboot.autoconfigure.etcd.EtcdAutoConfiguration;
import com.dragon.study.springboot.autoconfigure.etcd.EtcdRegister;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;


@Configuration
@AutoConfigureAfter({ThriftAutoConfiguration.class, EtcdAutoConfiguration.class})
@EnableConfigurationProperties({ThriftServerProperties.class})
@Slf4j
public class ThriftRegisterConfiguration {

  private final Pattern DEFAULT_PACKAGE_PATTERN = Pattern.compile(
      "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)*\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnExpression("#{environment.containsProperty('thrift.server.port')}")
  public EtcdRegister etcdRegister(EtcdClient etcdClient,
      ThriftServerProperties thriftServerProperties) {
    EtcdRegister register = new EtcdRegister();
    String serviceName = thriftServerProperties.getServiceName();

    int lastComma = serviceName.lastIndexOf(".");
    String interfaceName = serviceName.substring(0, lastComma);
    Assert.isTrue(DEFAULT_PACKAGE_PATTERN.matcher(interfaceName).matches(),
        "interface name is not match to package pattern");

    register.setPath(thriftServerProperties.getPrefixPath() + interfaceName);

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
