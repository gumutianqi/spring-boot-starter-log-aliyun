# spring-boot-starter-log-aliyun
基于阿里云日志服务，aliyun-log-producer 封装的 spring-boot starter 组件支持

### 使用说明

> 参考：https://yq.aliyun.com/articles/682761
> 
> 参考：https://github.com/aliyun/aliyun-log-java-producer

**已经发布到 Maven 中央仓库，直接引用以下依赖**

```xml
<dependency>
    <groupId>me.weteam</groupId>
    <artifactId>spring-boot-starter-logging-aliyun</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 版本更新记录

**2022-03-14 -> 2.0.0**
- `fix` 修复 SLS 写入 LogMap 强制要求 Map<String, String> 类型的问题
- `enhance` 调整 SLS 配置维度，只能操作单个 Project 下的 LogStore
- `feat` 升级支持 SpringBoot 2.x
- `feat` 升级支持 OpenJDK-11

**2019-05-22 -> 1.0.1**

- `feat` 新增`plugin`模块，后续陆续集成基于面向对象的插件化数据采集模式
- `feat` 新增插件：基于 ThreadLocal 的 Event 采集；使用该插件，可以采集一个线程栈过程中的 Event 事件数据
- `feat` 新增 ThreadLocal-event 插件文档

**2019-05-20 -> 1.0.0**

- `release` 第一个版本，集成 `aliyun-log-producer`，完成 spring-boot-starer 适配
- `feat` 实现自动配置，开箱即用；具体参考下文的配置文档和使用文档


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
    project-name: 必填 <AliyunLog Project Name>
    endpoint: 必填 cn-hangzhou.log.aliyuncs.com
    access-key-id: 必填 <AccessKeyId>
    access-key-secret: 必填 <AccessKeySecret>
    sts-token: 可选：[STS-Token]
    user-agent: aliyun-log-java-producer [aliyun-log-java-producer]
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

    // 通过阿里云 SDK 可知，可以同时向多个 Log Project 发送日志；
    // 我们的建议是：在同一个 Log Project 下面创建多个 Log Store；

    private static final String REQUESTS_LOG_PROJECT_STORE = "api-request";
    private static final String EVENT_LOG_PROJECT_STORE = "api-event";

    /**
     * 发送一个 HTTP Requests 请求日志
     *
     * @param logMap
     */
    public void sendRequest(Map<String, Object> logMap) {
        aliyunLogHub.send(REQUESTS_LOG_PROJECT_STORE, logMap);
    }

    /**
     * 发送一个 Event 事件 打点日志
     *
     * @param logMap
     */
    public void sendEvent(Map<String, Object> logMap) {
        aliyunLogHub.send(EVENT_LOG_PROJECT_STORE, logMap);
    }
    
    //.... 可扩展为封装其他类型的日志
}
```

**注意点**
由于阿里云 SLS 在写入 Log 数据时限制了 logMap 必须是 `Map<String, String>` 类型；
因此，`AliyunLogHub` 在最终写入前对 LogMap 做了如下处理，如果出现写入类型异常，关注这里；

```java
// AliyunLogHub.java - 102行
logMap.forEach((k, v) -> {
    String val = "null";
    if (!Objects.isNull(v)) {
        if (v instanceof String) {
            val = String.valueOf(v);
        } else {
            val = JSON.toJSONString(v);
        }
    }
    logItem.PushBack(k, val);
});
```

**使用建议：**

在项目的其他地方直接通过 IOC 注入 `LogBot` 进行日志采集即可；
为了规范采集日志内容格式，建议将以上 LogBot Class 中的日志采集内容 `logMap` 对象封装为强类型的 Java 业务对象；

---

> 如果其他建议，欢迎提供 Issues 和 pullRequest
