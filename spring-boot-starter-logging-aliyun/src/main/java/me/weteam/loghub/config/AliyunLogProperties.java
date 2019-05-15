/*
 * Copyright (c) 2019. WeTeam Inc. All Rights Reserved.
 *
 */

package me.weteam.loghub.config;

import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

/**
 * AliyunLog Properties 配置项
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2019-05-10 20:07
 * @since 3.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "aliyun.loghub")
public class AliyunLogProperties implements Serializable {
    private static final long serialVersionUID = 3255246417921427277L;

    /**
     * 是否开启 Aliyun Log Producer
     */
    private boolean enabled = false;

    /**
     * Aliyun Log Project 配置，可同时配置多个 Log Project
     */
    private List<AliyunLogProjectConfig> projects;

    /**
     * Aliyun Log Producer Config
     */
    private AliyunProducerConfig producer = new AliyunProducerConfig();


    /**
     * AliyunLog ProjectConfig Properties
     *
     * @author LarryKoo (larrykoo@126.com)
     * @slogon 站在巨人的肩膀上
     * @date 2019-05-13 14:51
     * @since 3.0.0
     */
    @Data
    @Validated
    public static class AliyunLogProjectConfig implements Serializable {
        private static final long serialVersionUID = 1368332269292883222L;

        /**
         * Aliyun Log Project Name
         */
        @NotBlank
        private String projectName;

        /**
         * Aliyun Log Project endpoint area
         */
        @NotBlank
        private String endpoint;

        /**
         * Aliyun accessKeyId
         */
        @NotBlank
        private String accessKeyId;

        /**
         * Aliyun accessKeySecret
         */
        @NotBlank
        private String accessKeySecret;
    }

    /**
     * LogProducer 相关的配置项
     *
     * @author LarryKoo (larrykoo@126.com)
     * @slogon 站在巨人的肩膀上
     * @date 2019-05-13 14:53
     * @since 3.0.0
     */
    @Data
    public static class AliyunProducerConfig implements Serializable {
        private static final long serialVersionUID = -3593242994012311721L;

        /**
         * 整型 - 字节，默认 100MB => 单个 producer 实例能缓存的日志大小上限。
         */
        private int totalSizeInBytes = ProducerConfig.DEFAULT_TOTAL_SIZE_IN_BYTES;

        /**
         * 整型 - 毫秒，默认 60 秒 => 如果 producer 可用空间不足，调用者在 send 方法上的最大阻塞时间，默认为 60 秒
         */
        private long maxBlockMs = ProducerConfig.DEFAULT_MAX_BLOCK_MS;

        /**
         * 整型 - 默认为可用处理器个数 => 执行日志发送任务的线程池大小。
         */
        private int ioThreadCount = ProducerConfig.DEFAULT_IO_THREAD_COUNT;

        /**
         * 整型 - 默认为 512 KB，最大可设置成 5MB => 当一个 ProducerBatch 中缓存的日志大小大于等于 batchSizeThresholdInBytes 时，
         * 该 batch 将被发送。
         */
        private int batchSizeThresholdInBytes = ProducerConfig.DEFAULT_BATCH_SIZE_THRESHOLD_IN_BYTES;

        /**
         * 整型 - 默认为 4096，最大可设置成 40960 => 当一个 ProducerBatch 中缓存的日志条数大于等于 batchCountThreshold 时，
         * 该 batch 将被发送。
         */
        private int batchCountThreshold = ProducerConfig.DEFAULT_BATCH_COUNT_THRESHOLD;

        /**
         * 整型 - 毫秒，默认 2 秒 => 一个 ProducerBatch 从创建到可发送的逗留时间，默认为 2 秒，最小可设置成 100 毫秒。
         */
        private int lingerMs = ProducerConfig.DEFAULT_LINGER_MS;

        /**
         * 整型 - 默认 10次 => 如果某个 ProducerBatch 首次发送失败，能够对其重试的次数，默认为 10 次。
         * 如果 retries 小于等于 0，该 ProducerBatch 首次发送失败后将直接进入失败队列。
         */
        private int retries = ProducerConfig.DEFAULT_RETRIES;

        /**
         * 整型 => 首次重试的退避时间，默认为 100 毫秒。
         * Producer 采样指数退避算法，第 N 次重试的计划等待时间为 baseRetryBackoffMs * 2^(N-1)。
         */
        private long baseRetryBackoffMs = ProducerConfig.DEFAULT_BASE_RETRY_BACKOFF_MS;

        /**
         * 整型 => 重试的最大退避时间，默认为 50 秒。
         */
        private long maxRetryBackoffMs = ProducerConfig.DEFAULT_MAX_RETRY_BACKOFF_MS;
    }
}
