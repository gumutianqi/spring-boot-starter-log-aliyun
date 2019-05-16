/*
 * Copyright (c) 2019. WeTeam Inc. All Rights Reserved.
 *
 */

package me.weteam.loghub;

import com.aliyun.openservices.aliyun.log.producer.Callback;
import com.aliyun.openservices.aliyun.log.producer.LogProducer;
import com.aliyun.openservices.aliyun.log.producer.errors.LogSizeTooLargeException;
import com.aliyun.openservices.aliyun.log.producer.errors.ProducerException;
import com.aliyun.openservices.aliyun.log.producer.errors.TimeoutException;
import com.aliyun.openservices.log.common.LogItem;
import lombok.extern.slf4j.Slf4j;
import me.weteam.loghub.config.AliyunLogProperties;

import java.util.Map;

/**
 * 基于 Aliyun Log Producer 封装
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2019-05-10 20:37
 * @since 3.0.0
 */
@Slf4j
public class AliyunLogHub {

    private LogProducer logProducer;

    public AliyunLogHub(LogProducer logProducer) {
        this.logProducer = logProducer;
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param logMap
     */
    public void send(String project, String logStore, Map<String, String> logMap) {
        this.send(project, logStore, "", "", logMap);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param project
     * @param logStore
     * @param topic
     * @param source
     * @param logMap
     */
    public void send(String project, String logStore, String topic, String source, Map<String, String> logMap) {
        this.send(project, logStore, topic, source, logMap, null);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param logMap 日志对象
     */
    public void send(String project, String logStore, String topic, String source, Map<String, String> logMap, Callback callback) {
        LogItem logItem = new LogItem();
        logMap.forEach(logItem::PushBack);
        try {
            if(logProducer != null) {
                logProducer.send(project, logStore, topic, source, logItem, callback);
            }
        } catch (InterruptedException e) {
            log.warn("The current thread has been interrupted during send logs.");
        } catch (ProducerException e) {
            if (e instanceof LogSizeTooLargeException) {
                log.error("The size of log is larger than the maximum allowable size", e);
            } else if (e instanceof TimeoutException) {
                log.error("The time taken for allocating memory for the logs has surpassed.", e);
            } else {
                log.error("Failed to send log, logItem={}", logItem, e);
            }
        } catch (Exception e) {
            log.error("Failed to send log, logItem={}", logItem, e);
        }
    }


}
