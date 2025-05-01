package com.tometrics.api.utils

fun String.toPascalCase(): String =
    this.split("_")
        .joinToString(" ") { it.lowercase() }
        .replaceFirstChar { it.uppercase() }
