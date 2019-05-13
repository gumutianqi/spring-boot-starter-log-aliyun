# spring-boot-starter-log-aliyun
基于阿里云日志服务，aliyun-log-producer 封装的 spring-boot starter 组件支持

### 使用说明

> 参考：https://yq.aliyun.com/articles/682761

```xml
<dependency>
    <groupId>me.weteam</groupId>
    <artifactId>spring-boot-starter-logging-aliyun</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置说明

```yaml
## Aliyun Log Hub
aliyun:
  loghub:
    enabled: true
    ## 参考阿里云 SDK 文档进行配置 Producer https://yq.aliyun.com/articles/682761
    producer:
      retries: 3
    ## 可以同时配置多个 aliyun log project
    projects:
      - project-name: <AliyunLog Project Name>
        endpoint: cn-hangzhou.log.aliyuncs.com
        access-key-id: <AccessKeyId>
        access-key-secret: <AccessKeySecret>
      - project-name: <AliyunLog Project Name>
        endpoint: cn-hangzhou.log.aliyuncs.com
        access-key-id: <AccessKeyId>
        access-key-secret: <AccessKeySecret>
```