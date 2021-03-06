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

import java.util.Date;
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
     * @param project  aliyun log project name
     * @param logStore aliyun log log store for project
     * @param logMap   aliyun log data
     */
    public void send(String project, String logStore, Map<String, String> logMap) {
        this.send(project, logStore, "", "", logMap);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param project  aliyun log project name
     * @param logStore aliyun log log store for project
     * @param topic    aliyun log topic
     * @param source   aliyun log source
     * @param logMap   aliyun log data
     */
    public void send(String project, String logStore, String topic, String source, Map<String, String> logMap) {
        this.send(project, logStore, topic, source, null, logMap, null);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param project  aliyun log project name
     * @param logStore aliyun log log store for project
     * @param mLogTime aliyun log time
     * @param logMap   aliyun log data
     */
    public void sendWithTime(String project, String logStore, Date mLogTime, Map<String, String> logMap) {
        this.sendWithTime(project, logStore, "", "", mLogTime, logMap);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param project  aliyun log project name
     * @param logStore aliyun log log store for project
     * @param topic    aliyun log topic
     * @param source   aliyun log source
     * @param mLogTime aliyun log time
     * @param logMap   aliyun log data
     */
    public void sendWithTime(String project, String logStore, String topic, String source, Date mLogTime, Map<String, String> logMap) {
        this.send(project, logStore, topic, source, mLogTime, logMap, null);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param project  aliyun log project name
     * @param logStore aliyun log log store for project
     * @param topic    aliyun log topic
     * @param source   aliyun log source
     * @param logMap   aliyun log data
     * @param callback aliyun log send result callback
     */
    public void send(String project, String logStore, String topic, String source, Date mLogTime, Map<String, String> logMap, Callback callback) {
        LogItem logItem;
        if (mLogTime != null) {
            logItem = new LogItem((int) mLogTime.getTime() / 1000);
        } else {
            logItem = new LogItem();
        }

        logMap.forEach(logItem::PushBack);
        try {
            if (logProducer != null) {
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
