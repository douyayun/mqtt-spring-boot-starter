package io.github.douyayun.mqtt.annotation;

import io.github.douyayun.mqtt.enums.QosEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

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
    QosEnum qos();

    /**
     * 消费者缓存队列大小
     *
     * @return
     */
    int cacheCapacity() default 100;
}
