package io.github.douyayun.mqtt.model;

import lombok.Builder;
import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Data
@Builder
public class MqttProcessObject {
    private String topic;
    private MqttMessage message;
}
