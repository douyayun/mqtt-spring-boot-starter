package io.github.douyayun.mqtt.config;

import io.github.douyayun.mqtt.processor.MqttPublishProcessor;
import io.github.douyayun.mqtt.properties.MqttPublisherProperties;
import io.github.douyayun.mqtt.processor.impl.MqttPublishProcessorImpl;
import io.github.douyayun.mqtt.service.pool.MqttPublishClientPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mqtt.publisher", name = "enable", havingValue = "true")
public class MqttPublisherConfiguration {
    @Autowired
    MqttPublisherProperties mqttPublisherProperties;

    @Bean
    protected MqttPublishClientPool mqttPublishClientPool() {
        return new MqttPublishClientPool(mqttPublisherProperties);
    }

    @Bean
    protected MqttPublishProcessor mqttPublishProcessor() {
        return new MqttPublishProcessorImpl(mqttPublisherProperties, mqttPublishClientPool());
    }
}