package com.undabot.izzy.parser

import com.undabot.izzy.models.Link

class DeserializeLink(private val deserializeMeta: DeserializeMeta = DeserializeMeta()) {

    fun from(jsonElements: JsonElements): Link? =
            when (jsonElements.isNull()) {
                true -> null
                false -> Link(hrefFrom(jsonElements), metaFrom(jsonElements))
            }

    private fun metaFrom(jsonElements: JsonElements): Map<String, Any?>? =
            deserializeMeta.from(jsonElements)

    private fun hrefFrom(jsonElements: JsonElements): String? =
            when (jsonElements.isObject()) {
                true -> jsonElements.stringFor("href")
                false -> jsonElements.asString()
            }
}