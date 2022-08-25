package com.undabot.izzy.models

import com.undabot.izzy.parser.SerializableDocument

data class JsonDocument<out T>(
    val data: T? = null,
    val links: Links? = null,
    val errors: Errors? = null,
    val meta: Map<String, Any?>? = null,
    val included: List<SerializableDocument>? = null,
) {

}
