package io.github.douyayun.mqtt.service.pool;

import io.github.douyayun.mqtt.properties.MqttPublisherProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.StringUtils;

/**
 * @Date: 2021/5/21 9:48
 */
@Slf4j
public class MqttPublishClient {

    private MqttClient mqttClient;

    private final MqttPublisherProperties mqttPublisherProperties;

    private final String clientId;

    private final String serverUrl;

    private final MqttConnectOptions options;

    public MqttPublishClient(int clientId, MqttPublisherProperties mqttPublisherProperties) {
        this.mqttPublisherProperties = mqttPublisherProperties;
        this.clientId = mqttPublisherProperties.getClientId() + "-" + clientId;
        serverUrl = "tcp://" + mqttPublisherProperties.getIp() + ":" + mqttPublisherProperties.getPort();

        options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(20);
        // 支持同时发送的消息数为1000. （默认值为10）
        options.setMaxInflight(1000);
        options.setConnectionTimeout(mqttPublisherProperties.getConnectionTimeout());
        if (StringUtils.hasText(mqttPublisherProperties.getUserName()) && StringUtils.hasText(mqttPublisherProperties.getPassword())) {
            options.setUserName(mqttPublisherProperties.getUserName());
            options.setPassword(mqttPublisherProperties.getPassword().toCharArray());
        }
    }

    /**
     * 链接
     *
     * @throws MqttException
     */
    public void connect() throws MqttException {
        mqttClient = new MqttClient(serverUrl, clientId, new MemoryPersistence());
        // 连接
        mqttClient.connect(options);
        log.info("mqtt publish客户端连接成功, ip:{}, 端口:{}, clientId:{}", mqttPublisherProperties.getIp(), mqttPublisherProperties.getPort(), clientId);
    }

    /**
     * 发布消息
     *
     * @param topic
     * @param message
     * @throws MqttException
     */
    public void publish(String topic, MqttMessage message) throws MqttException {
        mqttClient.publish(topic, message);
    }
}
