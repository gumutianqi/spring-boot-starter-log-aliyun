/*
 * Copyright (c) 2019. WeTeam Inc. All Rights Reserved.
 *
 */

package me.weteam.loghub.config;

import com.aliyun.openservices.aliyun.log.producer.LogProducer;
import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;
import com.aliyun.openservices.aliyun.log.producer.ProjectConfig;
import com.aliyun.openservices.aliyun.log.producer.ProjectConfigs;
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
 * @slogon 站在巨人的肩膀上
 * @date 2019-05-10 20:04
 * @since 3.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({
        LogProducer.class,
        AliyunLogHub.class
})
@ConditionalOnProperty(prefix = "aliyun.loghub", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(AliyunLogProperties.class)
public class AliyunLogAutoConfiguration {

    private AliyunLogProperties properties;

    public AliyunLogAutoConfiguration(AliyunLogProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建最终开箱即用的 AliyunLogHub 实例
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AliyunLogHub aliyunLogHub(LogProducer logProducer) {
        return new AliyunLogHub(logProducer);
    }

    /**
     * 创建 LogProducer 对象
     *
     * @return
     * @see {https://yq.aliyun.com/articles/682761}
     */
    @Bean
    @ConditionalOnMissingBean
    public LogProducer logProducer() {
        ProducerConfig producerConfig = new ProducerConfig(projectConfigs());

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

        return new LogProducer(producerConfig);
    }

    /**
     * 读取 Aliyun Log Project 配置，实例化 ProjectConfigs 对象
     *
     * @return
     */
    private ProjectConfigs projectConfigs() {
        ProjectConfigs projectConfigs = new ProjectConfigs();

        properties.getProjects().stream().map(item -> new ProjectConfig(
                item.getProjectName(), item.getEndpoint(),
                item.getAccessKeyId(), item.getAccessKeySecret())).forEach(projectConfigs::put);

        return projectConfigs;
    }
}
