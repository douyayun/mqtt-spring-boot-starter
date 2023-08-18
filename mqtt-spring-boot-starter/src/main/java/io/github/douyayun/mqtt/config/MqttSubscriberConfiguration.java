package io.github.douyayun.mqtt.config;

import io.github.douyayun.mqtt.properties.MqttSubscriberProperties;
import io.github.douyayun.mqtt.service.MqttSubscribeClientService;
import io.github.douyayun.mqtt.service.MqttSubscriberRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mqtt.subscriber", name = "enable", havingValue = "true")
public class MqttSubscriberConfiguration {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    MqttSubscriberProperties mqttSubscriberProperties;
    @Autowired
    MqttSubscriberRegister mqttSubscriberRegister;

    @Bean
    protected static MqttSubscriberRegister mqttSubscriberRegister() {
        return new MqttSubscriberRegister();
    }

    @Bean
    protected MqttSubscribeClientService mqttSubscribeClientService() {
        return new MqttSubscribeClientService(applicationContext, mqttSubscriberProperties, mqttSubscriberRegister);
    }
}
