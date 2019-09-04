package com.undabot.izzy.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType.ARRAY
import com.fasterxml.jackson.module.kotlin.readValue
import com.undabot.izzy.parser.JsonElements

class JacksonElements(private val jsonNode: JsonNode?) : JsonElements {

    override fun isArray(): Boolean {
        return jsonNode?.nodeType == ARRAY
    }

    override fun has(keyToFind: String): Boolean = jsonNode?.has(keyToFind) ?: false

    override fun jsonElementsArray(keyToFind: String): List<JsonElements> {
        val node = jsonNode?.findValue(keyToFind)!!
        return node.map { JacksonElements(it) }
    }

    override fun jsonElement(keyToFind: String): JsonElements {
        return JacksonElements(jsonNode?.findValue(keyToFind))
    }

    override fun asJsonString(): String {
        return jsonNode.toString()
    }

    override fun asArray(): List<JsonElements> {
        return jsonNode?.map { JacksonElements(it) }!!
    }

    override fun hasNonNull(key: String) = jsonNode?.hasNonNull(key) ?: false

    override fun isNull() = jsonNode == null || jsonNode.isNull

    override fun stringFor(key: String): String? = jsonNode?.get(key)?.asText()

    override fun isObject(): Boolean {
        return jsonNode?.isObject ?: false
    }

    override fun asString(): String? {
        return jsonNode?.asText()
    }

    override fun asMap(): Map<String, *>? {
        return ObjectMapper().readValue(this.asJsonString())
    }
}
