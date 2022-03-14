/*
 * Copyright (c) 2019. WeTeam Inc. All Rights Reserved.
 *
 */

package me.weteam.loghub;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.aliyun.log.producer.Callback;
import com.aliyun.openservices.aliyun.log.producer.LogProducer;
import com.aliyun.openservices.aliyun.log.producer.errors.LogSizeTooLargeException;
import com.aliyun.openservices.aliyun.log.producer.errors.ProducerException;
import com.aliyun.openservices.aliyun.log.producer.errors.TimeoutException;
import com.aliyun.openservices.log.common.LogItem;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * <p>基于 Aliyun Log Producer 封装</p>
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2019-05-10 20:37
 * @since 3.0.0
 */
@Slf4j
public class AliyunLogHub {

    private LogProducer logProducer;

    private String projectName;

    public AliyunLogHub(LogProducer logProducer, String projectName) {
        this.logProducer = logProducer;
        this.projectName = projectName;
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param logStore aliyun log log store for project
     * @param logMap   aliyun log data
     */
    public void send(String logStore, Map<String, Object> logMap) {
        this.send(logStore, "", "", logMap);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param logStore aliyun log log store for project
     * @param topic    aliyun log topic
     * @param source   aliyun log source
     * @param logMap   aliyun log data
     */
    public void send(String logStore, String topic, String source, Map<String, Object> logMap) {
        this.send(logStore, topic, source, null, logMap, null);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param logStore aliyun log log store for project
     * @param mLogTime aliyun log time
     * @param logMap   aliyun log data
     */
    public void sendWithTime(String logStore, Date mLogTime, Map<String, Object> logMap) {
        this.sendWithTime(logStore, "", "", mLogTime, logMap);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param logStore aliyun log log store for project
     * @param topic    aliyun log topic
     * @param source   aliyun log source
     * @param mLogTime aliyun log time
     * @param logMap   aliyun log data
     */
    public void sendWithTime(String logStore, String topic, String source, Date mLogTime, Map<String, Object> logMap) {
        this.send(logStore, topic, source, mLogTime, logMap, null);
    }

    /**
     * 发送一个 K/V 日志对象
     *
     * @param logStore aliyun log log store for project
     * @param topic    aliyun log topic
     * @param source   aliyun log source
     * @param mLogDate aliyun log time
     * @param logMap   aliyun log data
     * @param callback aliyun log send result callback
     */
    public void send(String logStore, String topic, String source, Date mLogDate, Map<String, Object> logMap, Callback callback) {
        LogItem logItem = new LogItem();
        if (mLogDate != null) {
            logItem.SetTime((int) mLogDate.getTime() / 1000);
        }

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

        try {
            if (logProducer != null) {
                logProducer.send(projectName, logStore, topic, source, logItem, callback);
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
