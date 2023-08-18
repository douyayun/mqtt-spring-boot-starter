package io.github.douyayun.mqtt.annotation;

import io.github.douyayun.mqtt.enums.QosEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 订阅主题
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MqttSubscriber {
    /**
     * 订阅主题
     *
     * @return
     */
    String topic();

    /**
     * qos
     *
     * @return
     */
    QosEnum qos() default QosEnum.Q_2_EXACTLY_ONCE;

    /**
     * 消费者缓存队列大小
     *
     * @return
     */
    int cacheCapacity() default 100;
}
