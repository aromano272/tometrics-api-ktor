package com.tometrics.api.services.achievement.config;

import com.tometrics.api.services.commonservice.ChannelProviderKt;
import com.tometrics.api.services.commonservice.Message;
import com.tometrics.api.services.commonservice.RoutingKey;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "achievement.service.queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(ChannelProviderKt.getRABBIT_MQ_EXCHANGE_NAME());
    }

    @Bean
    public Declarables bindings(Queue queue, DirectExchange exchange) {
        var events = List.of(
                Message.PostCreated.class,
                Message.CommentCreated.class
        );
        var bindings = events.stream()
                .map(clazz -> {
                    var annotation = clazz.getAnnotation(RoutingKey.class);
                    if (annotation == null) throw new IllegalArgumentException(String.format("Missing @RoutingKey annotation on %s", clazz.getSimpleName()));
                    var routingKey = annotation.value();
                    return BindingBuilder.bind(queue).to(exchange)
                            .with(routingKey);
                })
                .toList();
        return new Declarables(bindings);
    }


}
