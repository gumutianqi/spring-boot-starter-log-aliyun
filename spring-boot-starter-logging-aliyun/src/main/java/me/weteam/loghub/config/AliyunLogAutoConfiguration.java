/*
 * Copyright (c) 2019. WeTeam Inc. All Rights Reserved.
 *
 */

package me.weteam.loghub.config;

import com.aliyun.openservices.aliyun.log.producer.LogProducer;
import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;
import com.aliyun.openservices.aliyun.log.producer.ProjectConfig;
import lombok.extern.slf4j.Slf4j;
import me.weteam.loghub.AliyunLogHub;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LogHub 工具，构建 Log 采集对象
 *
 * @author LarryKoo (larrykoo@126.com)
 * @date 2019-05-10 20:04
 * @slogon 站在巨人的肩膀上
 * @since 3.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({
        LogProducer.class,
        AliyunLogHub.class
})
@ConditionalOnProperty(prefix = "aliyun.sls", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(AliyunLogProperties.class)
public class AliyunLogAutoConfiguration {

    private AliyunLogProperties properties;

    public AliyunLogAutoConfiguration(AliyunLogProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建最终开箱即用的 AliyunLogHub 实例
     *
     * @param logProducer LogProducer 实例
     * @return AliyunLogHub
     */
    @Bean
    @ConditionalOnMissingBean
    public AliyunLogHub aliyunLogHub(LogProducer logProducer) {
        return new AliyunLogHub(logProducer, properties.getProjectName());
    }

    /**
     * 创建 LogProducer 对象;
     * 参考：https://yq.aliyun.com/articles/682761
     *
     * @return LogProducer
     */
    @Bean
    @ConditionalOnMissingBean
    public LogProducer logProducer() {
        ProducerConfig producerConfig = new ProducerConfig();

        // 初始化 {@link AliyunProducerConfig}
        AliyunLogProperties.AliyunProducerConfig producerProp = properties.getProducer();
        producerConfig.setTotalSizeInBytes(producerProp.getTotalSizeInBytes());
        producerConfig.setMaxBlockMs(producerProp.getMaxBlockMs());
        producerConfig.setIoThreadCount(producerProp.getIoThreadCount());
        producerConfig.setBatchSizeThresholdInBytes(producerProp.getBatchSizeThresholdInBytes());
        producerConfig.setBatchCountThreshold(producerProp.getBatchCountThreshold());
        producerConfig.setLingerMs(producerProp.getLingerMs());
        producerConfig.setRetries(producerProp.getRetries());
        producerConfig.setBaseRetryBackoffMs(producerProp.getBaseRetryBackoffMs());
        producerConfig.setMaxRetryBackoffMs(producerProp.getMaxRetryBackoffMs());

        // 初始化 {@link LogProducer}
        LogProducer producer = new LogProducer(producerConfig);
        producer.putProjectConfig(ofProjectConfig());

        return producer;
    }

    /**
     * 读取 Aliyun Log Project 配置，实例化 ProjectConfigs 对象
     *
     * @return ProjectConfigs
     */
    private ProjectConfig ofProjectConfig() {
        return new ProjectConfig(
                properties.getProjectName(),
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret(),
                properties.getStsToken(),
                properties.getUserAgent());
    }
}
