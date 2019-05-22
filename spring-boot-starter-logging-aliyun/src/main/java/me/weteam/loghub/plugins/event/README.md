# ThreadLocal Event 插件使用

基于 ThreadLocal 的 Event 采集；使用该插件，可以采集一个线程栈过程中的 Event 事件数据；

### 使用说明

使用时，需要开发者创建一个类继承于 Event 事件抽象类：`AbstractLogEvent`,如：

```java
/**
 * 登录事件详细情况记录对象 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class LogAuthEvent extends AbstractLogEvent {
    private static final long serialVersionUID = 2411528799552959984L;

    // 登录所用的账号
    private String account;

    // 登录账号的类型
    private Integer accountType;

    // 登录客户端平台,ios | android | pc
    private String fromPlatform;

    // 登录客户端的版本
    private String clientVersion;
}
```

> 新建一个 `LogEventService` 用于自定义 logstore 等参数进行最后的日志上报

```java
/**
 * 用户行为事件日志采集
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2019-05-21 10:18
 * @since 3.0.0
 */
@Component
public class LogEventService {

    private final AliyunLogHub aliyunLogHub;

    private static final String EVENT_LOG_PROJECT = "<Log Project Name>";
    private static final String EVENT_LOG_PROJECT_STORE = "<Log Store Name>";
    private static final String EVENT_LOG_SOURCE = "<Log Source>";

    public LogEventService(AliyunLogHub aliyunLogHub) {
        this.aliyunLogHub = aliyunLogHub;
    }

    /**
     * 发送一个 Event 日志
     *
     * @param topic 主题
     * @param event 日志事件对象
     */
    private void send(String topic, AbstractLogEvent event) {
        aliyunLogHub.send(EVENT_LOG_PROJECT, EVENT_LOG_PROJECT_STORE, topic, EVENT_LOG_SOURCE, event.toMap());
    }

    /**
     * 从 MonitorEventHolder 读取 Event 进行发送
     *
     * @param topic 主题
     */
    public void send(String topic) {
        LogAuthEvent event = MonitorEventHolder.get(topic);
        if (event != null) {
            this.send(topic, event);
        }
    }
}
```

### 开始采集

> 采集过程可以是当前请求线程内的任意方法或任意位置；

**第一步：基于 topic 初始化要采集的事件**

```
// 初始化事件对象建议放在 请求进来的 Filter 上，保证成功初始化，一次可初始化多个不同 Topic 的事件对象 
MonitorEventHolder.init("<TOPIC>", new LogAuthEvent());

// doFilter 之前进行初始化，doFilter 之后进行清理工作；
filterChain.doFilter(requestWrapper, response);

// 在 filter 的结束放里面，还需要进行一些清理工作；
MonitorEventHolder.remove();
```

**第二步：采集事件过程数据**

> 采集过程，主要为，通过 Topic 获取 Event 事件对象；
> 往 Event 对象填充数据，最后在 finally 中保存 Event 数据

```
public void xxxMethod() {
    LogAuthEvent event = (LogAuthEvent) MonitorEventHolder.get("<TOPIC>")
    
    try {
      event.setTraceId("<TraceId>");
      // 在业务过程中采集数据
      
      event.setAccount("<Account>");
      
      // ..... event.setXXX(...);
    } finally {
      // 在 finally 中保存采集到的数据；
      MonitorEventHolder.put("<TOPIC>", event);
    }
}

```

**第三步：调用阿里云 Log Producer 上报事件数据**

```
// 在整个事件结束或者完成的地方，进行事件的上报；

@Autowired
private LogEventService logEventService;

// ...
logEventService.send("<TOPIC NAME>");

```



### 使用说明

> 如果其他建议，欢迎提供 Issues 和 pullRequest
