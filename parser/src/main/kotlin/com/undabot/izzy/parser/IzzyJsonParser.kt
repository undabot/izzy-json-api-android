package com.undabot.izzy.parser

import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument

interface IzzyJsonParser {
    fun parseToJsonElements(json: String): JsonElements
    fun <T : IzzyResource> parse(json: String, type: Class<*>): T
    fun izzyConfiguration(): IzzyConfiguration
    fun documentToJson(serializableDocument: JsonDocument<SerializableDocument>): String
    fun documentCollectionToJson(serializableDocument: JsonDocument<List<SerializableDocument>>): String
}

interface JsonElements {
    fun has(keyToFind: String): Boolean
    fun jsonElementsArray(keyToFind: String): List<JsonElements>
    fun jsonElement(keyToFind: String): JsonElements
    fun asJsonString(): String
    fun isArray(): Boolean
    fun isObject(): Boolean
    fun asArray(): List<JsonElements>
    fun stringFor(key: String): String?
    fun hasNonNull(key: String): Boolean
    fun isNull(): Boolean
    fun asString(): String?
    fun asMap(): Map<String, *>?
}
