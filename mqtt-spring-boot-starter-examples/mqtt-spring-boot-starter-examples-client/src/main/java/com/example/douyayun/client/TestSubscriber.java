package com.example.douyayun.client;

import io.github.douyayun.mqtt.annotation.MqttSubscriber;
import io.github.douyayun.mqtt.processor.MqttSubscribeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
@MqttSubscriber(topic = "test1/11")
public class TestSubscriber implements MqttSubscribeProcessor {
    @Override
    public void process(String topic, MqttMessage message) throws InterruptedException {
        log.info("topic:{}, message:{}", topic, new String(message.getPayload()));
        // TimeUnit.MILLISECONDS.sleep(1000 * 2);
    }
}
