package com.undabot.izzy.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument
import com.undabot.izzy.parser.IzzyConfiguration
import com.undabot.izzy.parser.IzzyJsonParser
import com.undabot.izzy.parser.JsonElements
import com.undabot.izzy.parser.SerializableDocument

class JacksonParser(
    private val izzyConfiguration: IzzyConfiguration = IzzyConfiguration(),
    private val objectMapper: ObjectMapper =
        ObjectMapper().apply { this.setSerializationInclusion(JsonInclude.Include.NON_NULL) }
) : IzzyJsonParser {

    override fun documentCollectionToJson(serializableDocument: JsonDocument<List<SerializableDocument>>): String {
        return serialize(serializableDocument)
    }

    override fun documentToJson(serializableDocument: JsonDocument<SerializableDocument>): String {
        return serialize(serializableDocument)
    }

    private fun serialize(serializableDocument: JsonDocument<Any>) =
        objectMapper.writer().writeValueAsString(serializableDocument).trim()

    override fun izzyConfiguration() = izzyConfiguration

    override fun parseToJsonElements(json: String): JsonElements {
        return JacksonElements(objectMapper.readTree(json))
    }

    override fun <T : IzzyResource> parse(json: String, type: Class<*>): T {
        return objectMapper.readValue(json, TypeFactory.defaultInstance().constructType(type))
    }
}
