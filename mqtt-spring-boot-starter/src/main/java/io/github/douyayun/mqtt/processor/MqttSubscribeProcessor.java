package io.github.douyayun.mqtt.processor;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * mqtt 消息消费者
 */
public interface MqttSubscribeProcessor {
    /**
     * 消费方法
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    void process(String topic, MqttMessage message) throws Exception;
}
