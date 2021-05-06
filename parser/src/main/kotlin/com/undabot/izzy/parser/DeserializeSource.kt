package com.undabot.izzy.parser

import com.undabot.izzy.models.Source

class DeserializeSource {

    fun from(jsonElements: JsonElements): Source? =
        when (jsonElements.isNull()) {
            true -> null
            false -> parseSourceFrom(jsonElements)
        }

    private fun parseSourceFrom(jsonElements: JsonElements) = Source(
        pointer = jsonElements.stringFor("pointer"),
        parameter = jsonElements.stringFor("parameter")
    )
}
