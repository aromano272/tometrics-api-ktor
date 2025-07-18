package com.tometrics.api.services.commonservice

import com.rabbitmq.client.AMQP
import com.tometrics.api.common.route.models.UserDto
import com.tometrics.api.services.protos.CommentCreated
import com.tometrics.api.services.protos.Event
import com.tometrics.api.services.protos.EventUser
import com.tometrics.api.services.protos.PostCreated
import io.ktor.util.logging.*
import kotlinx.coroutines.withContext
import kotlin.reflect.full.findAnnotation

interface EventProducer {
    suspend fun <T : Message> sendMessage(message: T)
}

class DefaultEventProducer(
    val logger: Logger,
    val channelProvider: ChannelProvider,
) : EventProducer {

    override suspend fun <T : Message> sendMessage(message: T) = withContext(channelProvider.dispatcher) {
        val clazz = message::class
        val annotation = clazz.findAnnotation<RoutingKey>()
            ?: error("Missing @RoutingKey annotation on ${clazz.simpleName}")
        val routingKey = annotation.value

        val chan = channelProvider.getChannel()

        chan.basicPublish(
            /* exchange = */ RABBIT_MQ_EXCHANGE_NAME,
            /* routingKey = */ routingKey,
            /* mandatory = */ true,
            /* props = */
            AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(2)
                .build(),
            /* body = */ message.toProto().toByteArray(),
        )
        logger.info("RabbitMQ Producer send message $message")
    }

}

fun Message.toProto(): Event = when (this) {

    is Message.PostCreated -> PostCreated.newBuilder()
        .setPostId(id)
        .setUser(user.toProto())
        .setText(text)
        .build()
        .let {
            Event.newBuilder()
                .setPostCreated(it)
                .build()
        }

    is Message.CommentCreated -> CommentCreated.newBuilder()
        .setCommentId(id)
        .setPostId(postId)
        .setPostUserId(postUserId)
        .setUser(user.toProto())
        .setText(text)
        .build()
        .let {
            Event.newBuilder()
                .setCommentCreated(it)
                .build()
        }

}

private fun UserDto.toProto() = EventUser.newBuilder()
    .setId(id)
    .setName(name)
    .build()