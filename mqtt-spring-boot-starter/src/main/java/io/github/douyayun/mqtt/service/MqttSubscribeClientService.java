package io.github.douyayun.mqtt.service;

import io.github.douyayun.mqtt.annotation.MqttSubscriber;
import io.github.douyayun.mqtt.enums.QosEnum;
import io.github.douyayun.mqtt.model.MqttProcessObject;
import io.github.douyayun.mqtt.model.SubscriberInfo;
import io.github.douyayun.mqtt.processor.MqttSubscribeProcessor;
import io.github.douyayun.mqtt.properties.MqttSubscriberProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 消息订阅者服务
 * @author Administrator
 */
@Slf4j
public class MqttSubscribeClientService {

    private MqttClient mqttClient;

    private final MqttSubscriberProperties mqttSubscriberProperties;

    private final MqttConnectOptions options;

    private final ApplicationContext applicationContext;

    private final MqttCallback mqttCallback;

    private final List<SubscriberInfo> subscriberInfoList;

    private final String serverUrl;

    public MqttSubscribeClientService(ApplicationContext applicationContext, MqttSubscriberProperties mqttSubscriberProperties, MqttSubscriberRegister mqttSubscriberRegister) {
        this.mqttSubscriberProperties = mqttSubscriberProperties;
        this.applicationContext = applicationContext;
        serverUrl = "tcp://" + mqttSubscriberProperties.getIp() + ":" + mqttSubscriberProperties.getPort();
        subscriberInfoList = convert(mqttSubscriberRegister.getMqttSubscribeProcessorBeanNames());

        options = new MqttConnectOptions();
        options.setConnectionTimeout(mqttSubscriberProperties.getConnectionTimeout());
        if (StringUtils.hasText(mqttSubscriberProperties.getUserName()) && StringUtils.hasText(mqttSubscriberProperties.getPassword())) {
            options.setUserName(mqttSubscriberProperties.getUserName());
            options.setPassword(mqttSubscriberProperties.getPassword().toCharArray());
        }
        options.setKeepAliveInterval(20);
        // 支持同时发送的消息数为1000. （默认值为10）
        options.setMaxInflight(1000);
        options.setCleanSession(mqttSubscriberProperties.isEnableCleanSession());
        mqttCallback = getCallback(subscriberInfoList);
    }

    /**
     * 初始化
     *
     * @throws Exception
     */
    @PostConstruct
    private void init() throws Exception {
        if (CollectionUtils.isEmpty(subscriberInfoList)) {
            log.info("未扫描到Subscriber, 不初始化mqtt subscribe客户端");
            return;
        }
        try {
            log.info("开始初始化mqtt subscribe客户端, config:{}", mqttSubscriberProperties.toString());
            mqttClient = new MqttClient(serverUrl, mqttSubscriberProperties.getClientId(), new MemoryPersistence());
            // 设定分发器
            mqttClient.setCallback(mqttCallback);
            // 连接
            mqttClient.connect(options);
            log.info("mqtt subscribe客户端连接成功, ip:{}, 端口:{}, clientId:{}", mqttSubscriberProperties.getIp(), mqttSubscriberProperties.getPort(), mqttSubscriberProperties.getClientId());
            // 注册subscribers
            for (SubscriberInfo subscriberInfo : subscriberInfoList) {
                String topic = subscriberInfo.getTopic();
                int qos = subscriberInfo.getQos().getQos();
                mqttClient.subscribe(topic, qos);

                Thread thread = new Thread(new ConsumeRunnable(subscriberInfo));
                thread.setName("mqtt-subscriber-" + subscriberInfo.getTopic());
                thread.start();

                log.info("mqtt subscribe客户端注册Subscriber topic:{},qos:{}", topic, qos);
            }

            log.info("mqtt subscribe客户端 所有Subscriber注册成功, topics:{}", subscriberInfoList.stream().map(SubscriberInfo::getTopic).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("初始化mqtt subscribe客户端失败, message:{}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 断线重连
     *
     * @param subscriberInfoList
     * @throws MqttException
     */
    private void reConnect(List<SubscriberInfo> subscriberInfoList) throws MqttException {
        mqttClient.close(true);
        mqttSubscriberProperties.setClientId(UUID.randomUUID().toString().replace("-",""));
        mqttClient = new MqttClient(serverUrl, mqttSubscriberProperties.getClientId(), new MemoryPersistence());
        // 设定分发器
        mqttClient.setCallback(mqttCallback);
        // 连接
        mqttClient.connect(options);
        // 注册subscribers
        for (SubscriberInfo subscriberInfo : subscriberInfoList) {
            String topic = subscriberInfo.getTopic();
            int qos = subscriberInfo.getQos().getQos();
            mqttClient.subscribe(topic, qos);
        }
    }

    /**
     * 回调函数
     *
     * @param orgSubscriberInfoList
     * @return
     */
    private MqttCallback getCallback(List<SubscriberInfo> orgSubscriberInfoList) {
        return new MqttCallback() {
            private final String ip = mqttSubscriberProperties.getIp();
            private final String port = mqttSubscriberProperties.getPort();
            private final int maxReconnectCount = mqttSubscriberProperties.getClientReconnectCount();
            private final long reconnectIntervalTime = mqttSubscriberProperties.getClientReconnectIntervalTime();
            private final List<SubscriberInfo> subscriberInfoList = orgSubscriberInfoList;

            /**
             * 丢失链接
             *
             * @param throwable the reason behind the loss of connection.
             */
            @Override
            public void connectionLost(Throwable throwable) {
                log.error("mqtt 失去连接 message:{}", throwable.getMessage(), throwable);
                if (!(throwable instanceof MqttException)) {
                    return;
                }
                MqttException mqttException = (MqttException) throwable;
                if (mqttException.getReasonCode() != MqttException.REASON_CODE_CONNECTION_LOST) {
                    return;
                }
                // 断开连接 重连
                for (int i = 1; i <= maxReconnectCount; i++) {
                    try {
                        log.info("mqtt subscribe客户端第{}/{}次尝试重连, 连接信息 ip:{}, 端口:{}", i, maxReconnectCount, ip, port);
                        reConnect(subscriberInfoList);
                        log.info("mqtt subscribe客户端第{}次重连成功, 连接信息 ip:{}, 端口:{}", i, ip, port);
                        TimeUnit.MILLISECONDS.sleep(reconnectIntervalTime);
                        return;
                    } catch (Exception e) {
                        log.error("mqtt subscribe客户端第{}/{}次尝试重连失败, 连接信息 ip:{}, 端口:{}, message:{}", i, maxReconnectCount, ip, port, e.getMessage(), e);
                    }
                }
                log.error("mqtt subscribe客户端重连失败, 重连次数达到{}次, 连接信息 ip:{}, 端口:{}", maxReconnectCount, ip, port);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                // 分发逻辑
                // for (int i = 0; i < subscriberInfoList.size(); i++) {
                //     SubscriberInfo subscriberInfo = subscriberInfoList.get(i);
                //     String matchStr = subscriberInfo.getTopic();
                //     if (matchStr.equals(topic) || (matchStr.endsWith("#") && topic.startsWith(matchStr.substring(0, matchStr.length() - 1)))) {
                //         // 分发到该队列
                //         log.info("mqtt 消息, topic:{}, qos:{}, aimTopic:{}", topic, message.getQos(), matchStr);
                //         try {
                //             subscriberInfo.getMessageQueue().add(MqttProcessObject.builder().topic(topic).message(message).build());
                //             // TimeUnit.MILLISECONDS.sleep(1);
                //         } catch (Exception e) {
                //             log.error("提交消息到队列失败, topic:{}, subscriber:{}, message:{}",
                //                     topic,
                //                     subscriberInfo.getMqttSubscribeProcessor().getClass().getName(),
                //                     e.getMessage(), e);
                //         }
                //         // 前移一位
                //         if (i != 0) {
                //             SubscriberInfo previous = subscriberInfoList.get(i - 1);
                //             subscriberInfoList.set(i - 1, subscriberInfo);
                //             subscriberInfoList.set(i, previous);
                //         }
                //         return;
                //     }
                // }
                // log.warn("主题消息没有相应的订阅处理器 topic:{}", topic);
            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        };
    }

    /**
     * 转换
     *
     * @param mqttSubscribeProcessorList
     * @return
     */
    private List<SubscriberInfo> convert(List<String> mqttSubscribeProcessorList) {
        return mqttSubscribeProcessorList.stream()
                .map(i -> (MqttSubscribeProcessor) applicationContext.getBean(i))
                .map(i -> {
                    MqttSubscriber mqttSubscriber = i.getClass().getAnnotation(MqttSubscriber.class);
                    String topic = mqttSubscriber.topic();
                    QosEnum qos = mqttSubscriber.qos();
                    int cacheCapacity = mqttSubscriber.cacheCapacity();
                    if (cacheCapacity <= 0) {
                        log.warn("{} 中 @MqttSubscriber(cacheCapacity={}), 该参数必须大于0, 已更改为配置文件默认值:{}", i.getClass().getName(), cacheCapacity, mqttSubscriberProperties.getQueueCapacity());
                        cacheCapacity = mqttSubscriberProperties.getQueueCapacity();
                    }
                    return SubscriberInfo.builder()
                            .topic(topic)
                            .qos(qos)
                            .mqttSubscribeProcessor(i)
                            .messageQueue(new ArrayBlockingQueue<>(cacheCapacity))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static class ConsumeRunnable implements Runnable {
        private final SubscriberInfo subscriberInfo;

        public ConsumeRunnable(SubscriberInfo subscriberInfo) {
            this.subscriberInfo = subscriberInfo;
        }

        @Override
        public void run() {
            while (true) {
                MqttProcessObject mqttProcessObject;
                try {
                    mqttProcessObject = subscriberInfo.getMessageQueue().take();
                } catch (InterruptedException e) {
                    log.error("mqtt 消费缓冲队列异常, message:{}", e.getMessage(), e);
                    throw new RuntimeException(e);
                }
                try {
                    subscriberInfo.getMqttSubscribeProcessor().process(mqttProcessObject.getTopic(), mqttProcessObject.getMessage());
                } catch (Exception e) {
                    log.error("mqtt 消费异常, message:{}", e.getMessage(), e);
                }
            }
        }
    }
}
