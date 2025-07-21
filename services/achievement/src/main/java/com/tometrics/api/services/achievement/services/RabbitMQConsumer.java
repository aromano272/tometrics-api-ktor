package com.tometrics.api.services.achievement.services;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.Channel;
import com.tometrics.api.services.achievement.config.RabbitMQConfig;
import com.tometrics.api.services.achievement.domain.models.AchievementType;
import com.tometrics.api.services.commonservice.EventConsumerKt;
import com.tometrics.api.services.commonservice.Message;
import com.tometrics.api.services.protos.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final AchievementService achievementService;

    public RabbitMQConsumer(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMessage(
            org.springframework.amqp.core.Message amqpMessage,
            Channel channel
    ) throws IOException {
        try {
            var event = Event.parseFrom(amqpMessage.getBody());
            var message = EventConsumerKt.toMessage(event);

            var handled = switch (message) {
                case Message.PostCreated postCreated -> {
                    achievementService.create(
                            postCreated.getUser().getId(),
                            AchievementType.POST_CREATED
                    );
                    yield true;
                }
                case Message.CommentCreated commentCreated -> {
                    achievementService.create(
                            commentCreated.getUser().getId(),
                            AchievementType.COMMENT_CREATED
                    );
                    yield true;
                }
                case null, default -> false;
            };
            // TODO(aromano): This early return may be an issue because i'm not ack/nack'ing the MQ, seeing that
            //  the message has been delivered even though we didn't handle it
            if (handled) {
                logger.info("RabbitMQ Consumer handled message: {}", message);
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("RabbitMQ Consumer failed to decode protobuf: {}", new String(amqpMessage.getBody()));
            e.printStackTrace();
            throw e;
        }
    }

}
