package io.github.douyayun.mqtt.config;

import io.github.douyayun.mqtt.processor.MqttPublishProcessor;
import io.github.douyayun.mqtt.properties.MqttPublisherProperties;
import io.github.douyayun.mqtt.service.MqttPublishProcessorImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mqtt.publisher", name = "enable", havingValue = "true")
public class MqttPublisherConfiguration {

    @Bean
    protected MqttPublisherProperties mqttPublisherProperties() {
        return new MqttPublisherProperties();
    }

    @Bean
    protected MqttPublishProcessor mqttPublishProcessor(MqttPublisherProperties mqttPublisherProperties) {
        return new MqttPublishProcessorImpl(mqttPublisherProperties);
    }
}