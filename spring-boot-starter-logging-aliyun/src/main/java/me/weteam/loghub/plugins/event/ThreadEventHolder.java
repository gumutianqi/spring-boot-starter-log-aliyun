/*
 * Copyright (c) 2019. WeTeam Inc. All Rights Reserved.
 *
 */

package me.weteam.loghub.plugins.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于 ThreadLocal 采集上下文的数据转换为
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2019-05-10 23:56
 * @since 3.0.0
 */
@Slf4j
public class ThreadEventHolder {

    private static final Map<String, AbstractLogEvent> EVENT_MAP = new HashMap<>();

    private static final ThreadLocal<Map<String, AbstractLogEvent>> EVENT_HOLDER =
            new NamedThreadLocal<>("LarryKoo plugins -> ThreadEventHolder");

    /**
     * 通过指定 topic 初始化事件
     *
     * @param topic 主题
     * @param event AbstractLogEvent
     */
    public static void init(String topic, AbstractLogEvent event) {
        EVENT_MAP.put(topic, event);
        EVENT_HOLDER.set(EVENT_MAP);
    }

    /**
     * 获取指定 topic 的 Event 对象
     *
     * @param topic 主题
     * @return AbstractLogEvent
     */
    public static AbstractLogEvent get(String topic) {
        return EVENT_HOLDER.get().get(topic);
    }

    /**
     * 往指定 topic 中 新增 Event 对象，可以多次 put，新值会覆盖旧值
     *
     * @param topic 主题
     * @param event AbstractLogEvent 泛型对象
     */
    public static void put(String topic, AbstractLogEvent event) {
        AbstractLogEvent contextEvent = EVENT_HOLDER.get().get(topic);
        try {
            merge(event, contextEvent);
            EVENT_MAP.put(topic, contextEvent);
            EVENT_HOLDER.set(EVENT_MAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * clean
     */
    public static void remove() {
        EVENT_HOLDER.remove();
    }

    /**
     * merge two bean by discovering differences
     *
     * @param target      目标对象
     * @param destination 结果对象
     * @param <M>         泛型
     * @throws Exception 抛出异常
     */
    private static <M> void merge(M target, M destination) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());

        // Iterate over all the attributes
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {

            // Only copy writable attributes
            if (descriptor.getWriteMethod() != null) {
                Object originalValue = descriptor.getReadMethod().invoke(target);

                // Only copy values values where the destination values is null
                if (originalValue == null) {
                    Object defaultValue = descriptor.getReadMethod().invoke(destination);
                    descriptor.getWriteMethod().invoke(target, defaultValue);
                }

            }
        }
    }
}
