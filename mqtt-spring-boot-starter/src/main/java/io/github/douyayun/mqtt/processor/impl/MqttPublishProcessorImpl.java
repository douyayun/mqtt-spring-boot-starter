package io.github.douyayun.mqtt.processor.impl;

import io.github.douyayun.mqtt.processor.MqttPublishProcessor;
import io.github.douyayun.mqtt.properties.MqttPublisherProperties;
import io.github.douyayun.mqtt.service.pool.MqttPublishClientPool;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * 消息发布处理器实现类
 */
public class MqttPublishProcessorImpl implements MqttPublishProcessor {

    private final MqttPublisherProperties mqttPublisherProperties;

    private final MqttPublishClientPool mqttPublishClientPool;

    public MqttPublishProcessorImpl(MqttPublisherProperties mqttPublisherProperties, MqttPublishClientPool mqttPublishClientPool) {
        this.mqttPublisherProperties = mqttPublisherProperties;
        this.mqttPublishClientPool = mqttPublishClientPool;
    }

    @PostConstruct
    private void init() {
        mqttPublishClientPool.init();
    }

    @Override
    public void publish(String topic, MqttMessage message) throws Exception {
        mqttPublishClientPool.publish(topic, message);
    }

    @Override
    public void publish(String topic, String message) throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes(StandardCharsets.UTF_8));
        mqttMessage.setQos(mqttPublisherProperties.getQos());
        mqttPublishClientPool.publish(topic, mqttMessage);
    }

    @Override
    public void publish(String topic, byte[] message) throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message);
        mqttMessage.setQos(mqttPublisherProperties.getQos());
        mqttPublishClientPool.publish(topic, mqttMessage);
    }
}
