package com.undabot.izzy.parser

class DeserializeErrorCustomProperties {

    fun from(jsonElements: JsonElements, ignoreProperties: List<String>): Map<String, Any?>? {
        val notIgnoredElements = jsonElements.asMap()?.toMutableMap()

        notIgnoredElements?.let { elements ->
            ignoreProperties.forEach {
                elements.remove(it)
            }
        }

        return if (notIgnoredElements.isNullOrEmpty()) null else notIgnoredElements
    }
}