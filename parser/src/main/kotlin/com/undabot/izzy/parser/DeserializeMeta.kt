package com.undabot.izzy.parser

class DeserializeMeta {

    fun from(jsonElements: JsonElements): Map<String, Any?>? =
            when (hasMetaIn(jsonElements)) {
                true -> jsonElements.jsonElement(META).asMap()
                false -> null
            }

    private fun hasMetaIn(jsonElements: JsonElements) =
            jsonElements.isObject() && jsonElements.hasNonNull(META)
}