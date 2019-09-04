package com.undabot.izzy.parser

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument

class GsonParser(
    private val izzyConfiguration: IzzyConfiguration = IzzyConfiguration(),
    private val gson: Gson
) : IzzyJsonParser {

    override fun parseToJsonElements(json: String): JsonElements {
        return GsonElements(gson.fromJson(json, JsonElement::class.java))
    }

    override fun <T : IzzyResource> parse(json: String, type: Class<*>): T {
        return gson.fromJson(json, type) as T
    }

    override fun izzyConfiguration(): IzzyConfiguration {
        return izzyConfiguration
    }

    override fun documentToJson(serializableDocument: JsonDocument<SerializableDocument>): String {
        return gson.toJson(serializableDocument)
    }

    override fun documentCollectionToJson(serializableDocument: JsonDocument<List<SerializableDocument>>): String {
        return gson.toJson(serializableDocument)
    }
}