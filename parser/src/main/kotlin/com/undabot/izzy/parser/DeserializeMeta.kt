package com.undabot.izzy.parser

class DeserializeMeta {

    fun fromRoot(jsonElements: JsonElements): Map<String, Any?>? {
        val root = jsonElements.asMap()

        return when (hasMetaIn(root)) {
            true -> root!![META] as Map<String, Any?>
            false -> null
        }
    }

    fun from(jsonElements: JsonElements): Map<String, Any?>? =
        when (hasMetaIn(jsonElements)) {
            true -> {
                jsonElements.jsonElement(META).asMap()
            }
            false -> null
        }

    private fun hasMetaIn(jsonElements: JsonElements) =
        jsonElements.isObject() && jsonElements.hasNonNull(META)

    private fun hasMetaIn(meta: Map<String, *>?) = meta?.contains(META) ?: false
}