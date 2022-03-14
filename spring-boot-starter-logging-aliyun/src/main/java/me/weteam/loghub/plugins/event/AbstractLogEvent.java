package me.weteam.loghub.plugins.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;

/**
 * <p>Event 事件对象规范协议</p>
 * <p>-</p>
 *
 * @author 阿古 (larrykoo@126.com)
 * @date 2022/2/28 10:09
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class AbstractLogEvent implements Serializable {
    private static final long serialVersionUID = 2392325212772370052L;

    /**
     * 事件ID 32位随机字符串
     */
    @JSONField(name = "_event_id")
    private String eventId;

    /**
     * 事件源类型
     */
    @JSONField(name = "_event_source_type")
    private String eventSourceType;

    /**
     * 事件源
     */
    @JSONField(name = "_event_source")
    private String eventSource;

    /**
     * 自定义枚举事件类型
     */
    @JSONField(name = "_event_type")
    private String eventType;

    /**
     * 事件 Topic [冗余信息]
     */
    @JSONField(name = "_event_topic")
    private String eventTopic;

    /**
     * 事件发生本地时间戳
     */
    @JSONField(name = "_event_occurred_time")
    private Long eventOccurredTime;

    /**
     * 事件生产本地时间戳
     */
    @JSONField(name = "_event_produced_time")
    private Long eventProducedTime;

    /**
     * 业务扩展元信息，事件生产者针对原始事件富化的内容
     */
    private Object meta;

    /**
     * 事件的原始消息内容
     */
    private Object data;

    //------------------------------------------------------------------------------
    //---------------------------- JSON 序列化工具 -----------------------------------
    //------------------------------------------------------------------------------

    /**
     * 返回对象JSON字符串
     *
     * @return JSON 字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    /**
     * 返回对象格式化的JSON字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String toJsonFormat() {
        return JSON.toJSONString(this, true);
    }

    /**
     * 转换对象为 MAP 对象
     *
     * @return Map 对象
     */
    public HashMap<String, Object> toMap() {
        return JSON.parseObject(toJson(), new TypeReference<>() {
        });
    }
}
