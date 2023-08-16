package io.github.douyayun.mqtt.model;

import io.github.douyayun.mqtt.enums.QosEnum;
import io.github.douyayun.mqtt.processor.MqttSubscribeProcessor;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.BlockingQueue;

@Data
@Builder
public class SubscriberInfo {
    private String topic;
    private QosEnum qos;
    private MqttSubscribeProcessor mqttSubscribeProcessor;
    private BlockingQueue<MqttProcessObject> messageQueue;
}
