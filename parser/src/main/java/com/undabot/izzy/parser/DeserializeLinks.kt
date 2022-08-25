package com.undabot.izzy.parser

import com.undabot.izzy.models.Links

class DeserializeLinks(private val deserializeLink: DeserializeLink = DeserializeLink()) {

    fun from(jsonElements: JsonElements): Links? =
        when (hasLinks(jsonElements)) {
            true -> parseLinksFrom(jsonElements)
            false -> null
        }

    private fun parseLinksFrom(jsonElements: JsonElements) = Links(
        self = linkFrom(jsonElements, "self"),
        first = linkFrom(jsonElements, "first"),
        last = linkFrom(jsonElements, "last"),
        prev = linkFrom(jsonElements, "prev"),
        next = linkFrom(jsonElements, "next"),
        related = linkFrom(jsonElements, "related")
    )

    private fun hasLinks(jsonElements: JsonElements) =
        jsonElements.has(LINKS) && jsonElements.hasNonNull(LINKS)

    private fun linkFrom(jsonElements: JsonElements, forKey: String) =
        deserializeLink.from(jsonElements.jsonElement(forKey))
}
