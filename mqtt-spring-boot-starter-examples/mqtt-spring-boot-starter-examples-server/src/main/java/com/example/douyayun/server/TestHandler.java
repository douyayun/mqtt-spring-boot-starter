package com.example.douyayun.server;

import io.github.douyayun.mqtt.annotation.MqttSubscriber;
import io.github.douyayun.mqtt.enums.QosEnum;

/**
 * TODO
 *
 * @author houp
 * @since 1.0.0
 **/
@MqttSubscriber(topic = "", qos = QosEnum.Q_0_AT_MOST_ONCE)
public class TestHandler {
}
