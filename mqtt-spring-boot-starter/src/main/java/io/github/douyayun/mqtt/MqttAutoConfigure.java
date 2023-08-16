package io.github.douyayun.mqtt;

import io.github.douyayun.mqtt.config.MqttPublisherConfiguration;
import io.github.douyayun.mqtt.config.MqttSubscriberConfiguration;
import io.github.douyayun.mqtt.properties.MqttPublisherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置
 *
 * @author houp
 * @since 1.0.0
 */
@Configuration
@ConditionalOnWebApplication
// @ConditionalOnClass(SignatureInterceptor.class)
@EnableConfigurationProperties(MqttPublisherProperties.class)
@Import({MqttPublisherConfiguration.class, MqttSubscriberConfiguration.class})
public class MqttAutoConfigure {
    private static final Logger log = LoggerFactory.getLogger(MqttAutoConfigure.class);


}
