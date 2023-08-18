package io.github.douyayun.mqtt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * 发布者配置
 *
 * @author houp
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "mqtt.publisher")
public class MqttPublisherProperties {

    @PostConstruct
    private void init() {
        if (enable) {
            String prefix = this.getClass().getAnnotation(ConfigurationProperties.class).prefix();
            if (!StringUtils.hasText(ip)) {
                throw new RuntimeException("publisher 未配置mqtt broker ip: {" + prefix + ".ip}");
            }
            if (!StringUtils.hasText(port)) {
                throw new RuntimeException("publisher 未配置mqtt broker 端口: {" + prefix + ".port}");
            }
            if (enableRandomClientId) {
                clientId = UUID.randomUUID().toString().replaceAll("-", "");
            } else if (!StringUtils.hasText(clientId)) {
                throw new RuntimeException("publisher mqtt客户端id是否随机生成配置为false时, 未配置mqtt 客户端id: {" + prefix + ".port}");
            }
            if (connectPoolSize <= 0) {
                throw new RuntimeException("publisher mqtt 发布连接池大小必须大于0: {" + prefix + ".port}");
            }
        }
    }

    /**
     * 是否开启mqtt客户端
     */
    private boolean enable = false;

    /**
     * mqtt broker ip
     */
    private String ip;

    /**
     * mqtt broker 端口
     */
    private String port;

    /**
     * mqtt broker 用户名
     */
    private String userName;

    /**
     * mqtt broker 密码
     */
    private String password;

    /**
     * mqtt 客户端id是否随机生成
     */
    private boolean enableRandomClientId = true;

    /**
     * mqtt 客户端id
     */
    private String clientId;

    /**
     * 连接超时时间(秒)
     */
    private int connectionTimeout = 5;

    /**
     * 默认Publisher Qos [0,1,2]
     */
    private int qos = 2;

    /**
     * 发布连接池大小
     */
    private int connectPoolSize = 10;

    /**
     * 发布消息从连接池获取链接超时时间(毫秒)
     */
    private long connectPoolPollTimeout = 3000L;


}
