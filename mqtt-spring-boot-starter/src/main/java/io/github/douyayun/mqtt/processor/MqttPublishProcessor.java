package io.github.douyayun.mqtt.processor;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 消息发布处理器
 */
public interface MqttPublishProcessor {
    /**
     * 发送消息
     *
     * @param topic
     * @param message
     */
    void publish(String topic, MqttMessage message) throws Exception;

    /**
     * 发送消息
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    void publish(String topic, String message) throws Exception;

    /**
     * 发送消息
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    void publish(String topic, byte[] message) throws Exception;
}
