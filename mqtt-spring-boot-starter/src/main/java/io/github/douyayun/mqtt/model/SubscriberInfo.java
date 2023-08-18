package io.github.douyayun.mqtt.model;

import io.github.douyayun.mqtt.enums.QosEnum;
import io.github.douyayun.mqtt.processor.MqttSubscribeProcessor;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.BlockingQueue;

/**
 * 订阅主题信息
 */
@Data
@Builder
public class SubscriberInfo {
    /**
     * 主题名称
     */
    private String topic;
    /**
     * 传输质量
     */
    private QosEnum qos;
    /**
     * 订阅主题处理器
     */
    private MqttSubscribeProcessor mqttSubscribeProcessor;
    /**
     * 订阅主题 收到的消息内容队列
     */
    private BlockingQueue<MqttProcessObject> messageQueue;
}
