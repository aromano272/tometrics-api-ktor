package com.tometrics.api.services.commongrpc

fun <T : com.google.protobuf.GeneratedMessage.Builder<T>> T.conditional(
    bool: Boolean,
    block: T.() -> T,
): T = if (bool) {
    block()
} else {
    this
}

fun <T : com.google.protobuf.GeneratedMessage.Builder<T>, D> T.ifLet(
    data: D?,
    block: T.(D) -> T,
): T = if (data != null) {
    block(data)
} else {
    this
}

