package io.github.douyayun.mqtt.service.pool;

import io.github.douyayun.mqtt.properties.MqttPublisherProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 发布客户端连接池
 */
@Slf4j
public class MqttPublishClientPool {

    private final MqttPublisherProperties mqttPublisherProperties;

    public MqttPublishClientPool(MqttPublisherProperties mqttPublisherProperties) {
        this.mqttPublisherProperties = mqttPublisherProperties;
    }

    private ArrayBlockingQueue<MqttPublishClient> mqttPublishClientQueue;

    /**
     * 初始化连接池
     */
    public void init() {
        log.info("mqtt publisher连接池, 开始初始化, 连接池大小:{}",
                mqttPublisherProperties.getConnectPoolSize());

        mqttPublishClientQueue = new ArrayBlockingQueue<>(
                mqttPublisherProperties.getConnectPoolSize(),
                false,
                IntStream.range(0, mqttPublisherProperties.getConnectPoolSize())
                        .parallel()
                        .mapToObj(i -> {
                            MqttPublishClient mqttPublishClient = new MqttPublishClient(i, mqttPublisherProperties);
                            try {
                                mqttPublishClient.connect();
                            } catch (MqttException e) {
                                throw new RuntimeException(e);
                            }
                            return mqttPublishClient;
                        })
                        .collect(Collectors.toList())
        );

        log.info("mqtt publisher连接池初始化完成, ip:{}, 端口:{}, clientId前缀:{}-, 连接池大小:{}",
                mqttPublisherProperties.getIp(),
                mqttPublisherProperties.getPort(),
                mqttPublisherProperties.getClientId(),
                mqttPublishClientQueue.size());
    }

    /**
     * 发布消息
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    public void publish(String topic, MqttMessage message) throws Exception {
        MqttPublishClient client = mqttPublishClientQueue.poll(mqttPublisherProperties.getConnectPoolPollTimeout(), TimeUnit.MILLISECONDS);
        if (client == null) {
            log.error("mqtt publisher连接池暂无就绪客户端, 当前连接池大小:{}", mqttPublisherProperties.getConnectPoolSize());
            throw new RuntimeException("mqtt publisher连接池暂无就绪客户端");
        }
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            throw e;
        } finally {
            mqttPublishClientQueue.put(client);
        }
    }
}
