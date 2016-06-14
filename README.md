# springboot-thrift-etcd-ribbon
基于springboot和thrift的简单rpc功能，目标就是简单、轻量

一般比如dubbo使用的zk做服务注册和发现，但是既然为了轻量级目标，就把zk替换成了etcd

路由算法目前选用了ribbon包的开源默认实现


##  项目组成
- thrift-server项目: 完成了thrift-server的启动，并且通过thrift-etcd把服务注册到etcd上面
- thrift-client项目：通过thrift-etcd找到通过thrift-server模块注册的服务，并且通过ribbon包进行路由算法选择路由
- thrift-etcd项目：为thrift-server提供注册功能，并且为thrift-cilent提供注册服务的列表
- example-server项目：thrift-server的事例项目
- exapmle-client项目：thrift-client的事例项目
- example-api项目：提供给thrift-server和thrift-client的api，通过thrift的idl生成的jar包
