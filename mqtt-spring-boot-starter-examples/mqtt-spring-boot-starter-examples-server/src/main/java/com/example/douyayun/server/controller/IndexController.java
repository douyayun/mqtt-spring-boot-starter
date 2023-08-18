package com.example.douyayun.server.controller;

import io.github.douyayun.mqtt.processor.MqttPublishProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author houp
 * @since 1.0.0
 **/
@RestController
@RequestMapping
public class IndexController {
    @Autowired
    MqttPublishProcessor mqttPublishProcessor;

    @GetMapping("")
    public String index() {
        return "emqx publish";
    }

    @GetMapping("test1")
    public String test1() throws Exception {
        mqttPublishProcessor.publish("test1/11", "test1/11消息 " + System.currentTimeMillis());
        return "ok";
    }

    @GetMapping("test2")
    public String test2() throws Exception {
        mqttPublishProcessor.publish("test2/11", "test2/11消息 " + System.currentTimeMillis());
        return "ok";
    }

}
