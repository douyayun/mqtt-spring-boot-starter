package io.github.douyayun.mqtt.service;

import io.github.douyayun.mqtt.annotation.MqttSubscriber;
import io.github.douyayun.mqtt.processor.MqttSubscribeProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date: 2021/5/20 14:53
 */
public class MqttSubscriberRegister implements PriorityOrdered, BeanFactoryPostProcessor {

    private List<String> mqttSubscribeProcessorBeanNames;

    protected List<String> getMqttSubscribeProcessorBeanNames() {
        return mqttSubscribeProcessorBeanNames;
    }

    /**
     * 初始化mqtt客户端并连接mqtt broker
     */

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] mqttSubscriberBeanNames = beanFactory.getBeanNamesForAnnotation(MqttSubscriber.class);
        Map<String, Class<?>> allTopicRef = new HashMap<>(mqttSubscriberBeanNames.length);
        for (String mqttSubscriberBeanName : mqttSubscriberBeanNames) {
            Class<?> mqttSubscriberClazz = beanFactory.getType(mqttSubscriberBeanName);
            assert mqttSubscriberClazz != null;
            Class<?>[] interfaces = mqttSubscriberClazz.getInterfaces();
            if (Arrays.stream(interfaces).noneMatch(i -> i == MqttSubscribeProcessor.class)) {
                throw new RuntimeException("mqtt SubscribeProcessor 必须实现 MqttSubscribeProcessor, 问题定义: [" +
                        mqttSubscriberClazz.getName() + "]");
            }
            MqttSubscriber mqttSubscriberConfig = mqttSubscriberClazz.getAnnotation(MqttSubscriber.class);
            String topic = mqttSubscriberConfig.topic();
            for (String historyTopic : allTopicRef.keySet()) {
                if (isDuplicate(historyTopic, topic)) {
                    throw new RuntimeException("mqtt 不允许重复或包含关系的主题订阅, 冲突定义: " +
                            "[" + historyTopic + ", " + topic + "],"+
                            "[" + allTopicRef.get(historyTopic).getName() + ", " + mqttSubscriberClazz.getName() + "]");
                }
            }
            allTopicRef.put(topic,mqttSubscriberClazz);
        }
        mqttSubscribeProcessorBeanNames = Arrays.asList(mqttSubscriberBeanNames);
    }

    private boolean isDuplicate(String a, String b) {
        if (!a.equals(b)) {
            boolean aIsSharpEnd = a.endsWith("#");
            boolean bIsSharpEnd = b.endsWith("#");
            if (!aIsSharpEnd && !bIsSharpEnd) {
                return false;
            } else if (aIsSharpEnd && !bIsSharpEnd) {
                return b.startsWith(a.substring(0, a.length() - 1));
            } else if (!aIsSharpEnd) {
                return a.startsWith(b.substring(0, b.length() - 1));
            } else {
                return b.startsWith(a.substring(0, a.length() - 1)) || a.startsWith(b.substring(0, b.length() - 1));
            }
        } else {
            return true;
        }
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
