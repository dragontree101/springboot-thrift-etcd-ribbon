# 说在前面
项目基于 java8 进行开发， thrift 使用的版本是0.9.3版本， etcd使用的是2.x的版本

# springboot-thrift-etcd-ribbon
基于springboot和thrift的简单rpc功能，目标就是简单、轻量

一般比如dubbo使用的zk做服务注册和发现，但是既然为了轻量级目标，就把zk替换成了etcd

路由算法目前选用了ribbon包的开源默认实现

# 使用方式

项目使用springboot的标准的starter的方式进行， 使用者通过引入etcd-thrift-starter项目，并且通过加入配置文件来制定配置


##  项目组成
- autoconfigure项目: 包括了server和client的主要逻辑，服务端的注册、客户端的寻址和路由
- etcd-thrift-starter项目：以springboot的starter的形式提供了依赖， 主要在pom.xml中加入依赖
- example-api项目：提供给thrift-server和thrift-client的api，通过thrift的idl生成的jar包
- example-server项目：thrift-server的实例项目，主要是暴露所提供的服务和注册节点到etcd
- exapmle-client项目：thrift-client的实例项目，主要是通过etcd获得服务节点，并且完成服务的路由和调用
