# spring-boot-starter-log-aliyun
基于阿里云日志服务，aliyun-log-producer 封装的 spring-boot starter 组件支持

### 使用说明

> 参考：https://yq.aliyun.com/articles/682761

**已经发布到 Maven 中央仓库，直接引用以下依赖**

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

### 使用说明

建议参考下面的代码进行使用；
新建一个 Class，作为日志采集的 IOC 工具

```java
import me.weteam.loghub.AliyunLogHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 日志采集
 */
@Component
public class LogBot {

    @Autowired
    private AliyunLogHub aliyunLogHub;

    // 通过阿里云 SDK 可知，可以同时想多个 Log Project 发送日志；
    // 我们的建议是：在同一个 Log Project 下面创建多个 Log Store；

    private static final String REQUESTS_LOG_PROJECT = "api-log";
    private static final String REQUESTS_LOG_PROJECT_STORE = "api-request";

    private static final String EVENT_LOG_PROJECT = "api-log";
    private static final String EVENT_LOG_PROJECT_STORE = "api-event";

    /**
     * 发送一个 HTTP Requests 请求日志
     *
     * @param logMap
     */
    public void sendRequest(Map<String, String> logMap) {
        aliyunLogHub.send(REQUESTS_LOG_PROJECT, REQUESTS_LOG_PROJECT_STORE, logMap);
    }

    /**
     * 发送一个 Event 事件 打点日志
     *
     * @param logMap
     */
    public void sendEvent(Map<String, String> logMap) {
        aliyunLogHub.send(EVENT_LOG_PROJECT, EVENT_LOG_PROJECT_STORE, logMap);
    }
    
    //.... 可扩展为封装其他类型的日志
}
```

**使用建议：**

在项目的其他地方直接通过 IOC 注入 `LogBot` 进行日志采集即可；
为了规范采集日志内容格式，建议将以上 LogBot Class 中的日志采集内容 `logMap` 对象封装为强类型的 Java 业务对象；

---

> 如果其他建议，欢迎提供 Issues 和 pullRequest
