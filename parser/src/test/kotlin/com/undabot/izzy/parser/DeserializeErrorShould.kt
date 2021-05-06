package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.models.Error
import com.undabot.izzy.models.Source
import org.junit.Test

class DeserializeErrorShould {

    private val deserializeError = DeserializeError()
    private lateinit var errorElement: JsonElements
    private lateinit var actualError: Error

    @Test
    fun `deserialize null 'id' from element to null 'id' in error`() {
        Given { `json error element as`("""{}""") }
        When { `deserialize is requested`() }
        Then { actualError.id equals null }
    }

    @Test
    fun `deserialize 'id' from element to 'id' in error`() {
        Given { `json error element as`("""{"id":"404"}""") }
        When { `deserialize is requested`() }
        Then { actualError.id equals "404" }
    }

    @Test
    fun `deserialize null 'status' from element to null 'status' in error`() {
        Given { `json error element as`("""{}""") }
        When { `deserialize is requested`() }
        Then { actualError.status equals null }
    }

    @Test
    fun `deserialize 'status' from element to 'status' in error`() {
        Given { `json error element as`("""{"status":"Some status"}""") }
        When { `deserialize is requested`() }
        Then { actualError.status equals "Some status" }
    }

    @Test
    fun `deserialize null 'code' from element to null 'code' in error`() {
        Given { `json error element as`("{}") }
        When { `deserialize is requested`() }
        Then { actualError.code equals null }
    }

    @Test
    fun `deserialize 'code' from element to 'code' in error`() {
        Given { `json error element as`("""{"code":"2"}""") }
        When { `deserialize is requested`() }
        Then { actualError.code equals "2" }
    }

    @Test
    fun `deserialize null 'title' from element to null 'title' in error`() {
        Given { `json error element as`("{}") }
        When { `deserialize is requested`() }
        Then { actualError.title equals null }
    }

    @Test
    fun `deserialize 'title' from element to 'title' in error`() {
        Given { `json error element as`("""{"title":"Error title"}""") }
        When { `deserialize is requested`() }
        Then { actualError.title equals "Error title" }
    }

    @Test
    fun `deserialize null 'detail' from element to 'title' in error`() {
        Given { `json error element as`("{}") }
        When { `deserialize is requested`() }
        Then { actualError.detail equals null }
    }

    @Test
    fun `deserialize 'detail' from element to 'detail' in error`() {
        Given { `json error element as`("""{"detail":"Error detail"}""") }
        When { `deserialize is requested`() }
        Then { actualError.detail equals "Error detail" }
    }

    @Test
    fun `deserialize null 'source' from element to null 'source' in error`() {
        Given { `json error element as`("""{}""") }
        When { `deserialize is requested`() }
        Then { actualError.source equals null }
    }

    @Test
    fun `deserialize 'source' from element to 'source' in error`() {
        Given { `json error element as`("""{"source":{"pointer":"pointer"}}""") }
        When { `deserialize is requested`() }
        Then { actualError.source equals Source(pointer = "pointer") }
    }

    @Test
    fun `deserialize null 'meta' from element to null 'meta' in error`() {
        Given { `json error element as`("{}") }
        When { `deserialize is requested`() }
        Then { actualError.meta equals null }
    }

    @Test
    fun `deserialize 'meta' from element to 'meta' in error`() {
        Given { `json error element as`("""{"meta":{"key":"value"}}""") }
        When { `deserialize is requested`() }
        Then { actualError.meta equals hashMapOf("key" to "value") }
    }

    @Test
    fun `deserialize 'customProperties' from element to 'meta' in error`() {
        Given { `json error element as`("""{"customError":{"key":"value"}}""") }
        When { `deserialize is requested`() }
        Then { actualError.customProperties equals hashMapOf("customError" to hashMapOf("key" to "value")) }
    }

    private fun `json error element as`(json: String) {
        errorElement = JacksonParser().parseToJsonElements(json)
    }

    private fun `deserialize is requested`() {
        actualError = deserializeError.from(errorElement)
    }
}
