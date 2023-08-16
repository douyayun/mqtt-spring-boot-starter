package io.github.douyayun.mqtt.exception;

/**
 * MQTT异常
 *
 * @author houp
 * @since 1.0.0
 */
public class MqttException extends RuntimeException {

    /**
     * 全局错误码
     */
    private Integer code = 1000;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public MqttException() {
    }

    public MqttException(String message) {
        this.message = message;
    }

    public MqttException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public MqttException setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public MqttException setMessage(String message) {
        this.message = message;
        return this;
    }

}
