package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.jackson.JacksonParser
import org.junit.Test

class DeserializeMetaShould {

    private val deserializeMeta = DeserializeMeta()
    private lateinit var rootElement: JsonElements
    private var actualMeta: Map<String, Any?>? = null

    @Test
    fun `return null when meta is not provided`() {
        Given { `root json element as`("{}") }
        When { `deserialize is requested`() }
        Then { actualMeta equals null }
    }

    @Test
    fun `return null when provided meta is null`() {
        Given { `root json element as`("""{"meta":null}""") }
        When { `deserialize is requested`() }
        Then { actualMeta equals null }
    }

    @Test
    fun `return parsed meta when meta is provided`() {
        Given { `root json element as`("""{"meta":{"meta_key":"meta_value"}}""") }
        When { `deserialize is requested`() }
        Then { actualMeta equals hashMapOf("meta_key" to "meta_value") }
    }

    private fun `deserialize is requested`() {
        actualMeta = deserializeMeta.from(rootElement)
    }

    private fun `root json element as`(json: String) {
        rootElement = JacksonParser().parseToJsonElements(json)
    }
}