package com.tometrics.api.services.commonservice

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.tometrics.api.common.route.models.UserDto
import com.tometrics.api.services.protos.Event
import com.tometrics.api.services.protos.EventUser
import io.ktor.server.application.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import org.koin.ktor.ext.get
import kotlin.reflect.full.findAnnotation

class ConsumerBuilder {
    val handlers: MutableMap<String, suspend (Message) -> Unit> = mutableMapOf()
}

inline fun <reified T : Message> ConsumerBuilder.registerEvent(
    crossinline handler: suspend (T) -> Unit
) {
    val clazz = T::class
    val annotation = clazz.findAnnotation<RoutingKey>()
        ?: error("Missing @RoutingKey annotation on ${clazz.simpleName}")
    val routingKey = annotation.value

    handlers[routingKey] = { msg ->
        @Suppress("UNCHECKED_CAST")
        handler(msg as T)
    }
}

fun Application.launchRabbitMQConsumer(
    queueName: String,
    builderFn: ConsumerBuilder.() -> Unit,
) = monitor.subscribe(ApplicationStarted) { application ->
    val logger: Logger = get()
    val channelProvider: ChannelProvider = get()

    val builder = ConsumerBuilder().apply(builderFn)
    val handlers = builder.handlers

    application.launch {
        val chan = channelProvider.getChannel()
        chan.queueDeclare(
            /* queue = */ queueName,
            /* durable = */ true,
            /* exclusive = */ false,
            /* autoDelete = */ false,
            /* arguments = */ null,
        )
        handlers.forEach { (routingKey, _) ->
            chan.queueBind(
                /* queue = */ queueName,
                /* exchange = */ RABBIT_MQ_EXCHANGE_NAME,
                /* routingKey = */ routingKey,
            )
        }

        chan.basicConsume(
            queueName,
            false,
            object : DefaultConsumer(chan) {
                override fun handleDelivery(
                    consumerTag: String,
                    envelope: Envelope,
                    properties: AMQP.BasicProperties,
                    body: ByteArray,
                ) {
                    val handler = handlers[envelope.routingKey] ?: return
                    try {
                        val message = Event.parseFrom(body)
                            .toMessage()!!
                        application.launch {
                            try {
                                handler(message)
                                // TODO(aromano): there might be a concurrency/threading issue calling this from the launch
                                logger.info("RabbitMQ Consumer handled message: $message")
                                chan.basicAck(
                                    /* deliveryTag = */ envelope.deliveryTag,
                                    /* multiple = */ false,
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                logger.error(ex)
                                logger.error("RabbitMQ Consumer failed to handle message: $message")
                                chan.basicNack(
                                    /* deliveryTag = */ envelope.deliveryTag,
                                    /* multiple = */ false,
                                    /* requeue = */ !envelope.isRedeliver,
                                )
                            }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        logger.error(ex)
                        logger.error("RabbitMQ Consumer failed to decode json: ${String(body)}")
                        chan.basicNack(
                            /* deliveryTag = */ envelope.deliveryTag,
                            /* multiple = */ false,
                            /* requeue = */ false,
                        )
                    }
                }
            }
        )
    }
}

fun Event.toMessage(): Message? = when {

    hasPostCreated() -> Message.PostCreated(
        id = postCreated.postId,
        user = postCreated.user.toUserDto(),
        text = postCreated.text,
    )

    hasCommentCreated() -> Message.CommentCreated(
        id = commentCreated.commentId,
        postId = commentCreated.postId,
        postUserId = commentCreated.postUserId,
        user = commentCreated.user.toUserDto(),
        text = commentCreated.text,
    )

    else -> null

}

fun EventUser.toUserDto(): UserDto = UserDto(
    id = id,
    name = name,
    location = null,
    climateZone = null,
    updatedAt = -1,
)
