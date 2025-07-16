package com.tometrics.api.services.commonservice

import com.rabbitmq.client.*
import io.ktor.util.logging.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

val RABBIT_MQ_EXCHANGE_NAME = "tometrics.events"

class ChannelProvider(
    private val logger: Logger,
) {
    val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val factory = ConnectionFactory()

    private val mutex = Mutex()

    private var connection: Connection? = null
    private var channel: Channel? = null

    // TODO(aromano): this shouldn't be necessary nor a SharedFlow<Channel> since auto recovery is enabled, but i need to test it out
    suspend fun getChannel(): Channel = withContext(dispatcher) {
        mutex.withLock {
            val conn = connection?.takeIf { it.isOpen }
                ?: factory.newConnection().also {
                    if (connection != null) logger.error("Existing connection was closed")
                    connection = it
                    it.addShutdownListener {
                        logger.info("RabbitMQ connection shutdown")
                    }
                    channel?.close()
                    channel = null
                }
            val chan = channel?.takeIf { it.isOpen }
                ?: conn.createChannel().also {
                    if (channel != null) logger.error("Existing channel was closed")
                    declareTopology(it)
                    it.addReturnListener { ret ->
                        logger.error("RabbitMQ returned message $ret")
                    }
                    it.addShutdownListener {
                        logger.info("RabbitMQ channel shutdown")
                    }
                    channel = it
                }

            chan
        }
    }

    private suspend fun declareTopology(chan: Channel) = withContext(dispatcher) {
        chan.exchangeDeclare(
            /* exchange = */ RABBIT_MQ_EXCHANGE_NAME,
            /* type = */ BuiltinExchangeType.DIRECT,
            /* durable = */ true,
            /* autoDelete = */ false,
            /* arguments = */ null,
        )
    }

}

