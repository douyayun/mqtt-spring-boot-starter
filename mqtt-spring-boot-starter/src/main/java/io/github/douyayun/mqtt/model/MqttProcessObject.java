package io.github.douyayun.mqtt.model;

import lombok.Builder;
import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 消息信息
 */
@Data
@Builder
public class MqttProcessObject {
    /**
     * 主题名称
     */
    private String topic;
    /**
     * 消息体
     */
    private MqttMessage message;
}
