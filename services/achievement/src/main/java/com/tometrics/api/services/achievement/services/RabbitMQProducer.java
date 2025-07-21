package com.tometrics.api.services.achievement.services;

import com.tometrics.api.services.achievement.config.RabbitMQConfig;
import com.tometrics.api.services.commonservice.ChannelProviderKt;
import com.tometrics.api.services.commonservice.EventProducerKt;
import com.tometrics.api.services.commonservice.Message;
import com.tometrics.api.services.commonservice.RoutingKey;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(Message message) {
        var routingKey = message.getClass().getAnnotation(RoutingKey.class).value();
        var proto = EventProducerKt.toProto(message);

        rabbitTemplate.convertAndSend(
                ChannelProviderKt.getRABBIT_MQ_EXCHANGE_NAME(),
                routingKey,
                proto.toByteArray()
        );
    }

}
