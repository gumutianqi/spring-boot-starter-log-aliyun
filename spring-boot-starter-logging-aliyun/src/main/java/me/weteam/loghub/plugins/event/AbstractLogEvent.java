/*
 * Copyright (c) 2019. WeTeam Inc. All Rights Reserved.
 *
 */

package me.weteam.loghub.plugins.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 事件日志对象
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2019-05-21 10:19
 * @since 3.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public abstract class AbstractLogEvent implements Serializable {
    private static final long serialVersionUID = 1776316984501205238L;

    /**
     * 事件所在的 TraceId
     */
    private String traceId;

    /**
     * 事件发生时当前的用户
     */
    private String currentUser;

    /**
     * 事件发生的位置
     */
    private String location;

    /**
     * 事件做了什么
     */
    private String action;

    /**
     * 事件相关的内容消息
     */
    private String message;

    /**
     * 事件处理结果
     */
    private String result;

    /**
     * 事件耗时; 单位：毫秒
     */
    private Long duration;

    /**
     * 返回对象JSON字符串
     *
     * @return json 字符串
     */
    public String toJSON() {
        return JSON.toJSONString(this);
    }

    /**
     * 返回对象格式化的JSON字符串
     *
     * @return json 字符串
     */
    public String toJSONFormat() {
        return JSON.toJSONString(this, true);
    }

    /**
     * 转换对象为 MAP 对象
     *
     * @return Map 对象
     */
    public Map<String, String> toMap() {
        return JSON.parseObject(toJSON(), new TypeReference<Map<String, String>>() {});
    }
}
