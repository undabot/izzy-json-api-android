package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import org.junit.Test

abstract class JsonElementsShould {

    private lateinit var jsonElements: JsonElements

    abstract fun `parseJsonToJsonElements`(json: String): JsonElements

    @Test
    fun `return true when given element is array`() {
        Given { `json elements from`("[]") }
        Then { jsonElements.isArray() equals true }
    }

    @Test
    fun `return false when given element is not array`() {
        Given { `json elements from`("{}") }
        Then { jsonElements.isArray() equals false }
    }

    @Test
    fun `return true when element contains key`() {
        Given { `json elements from`("""{"title":"Article"}""") }
        Then { jsonElements.has("title") equals true }
    }

    @Test
    fun `return false when element does not contain key`() {
        Given { `json elements from`("{}") }
        Then { jsonElements.has("title") equals false }
    }

    @Test
    fun `return list of json elements for given key`() {
        var elementsList: List<JsonElements>? = null
        Given { `json elements from`("""{"list":[{"title":"first title"},{"description":"second description"}]}""") }
        When { elementsList = jsonElements.jsonElementsArray("list") }
        Then {
            elementsList!![0].has("title") equals true
            elementsList!![1].has("description") equals true
        }
    }

    @Test
    fun `return json element object for given key`() {
        var element: JsonElements? = null
        Given { `json elements from`("""{"object":{"title":"object title"}}""") }
        When { element = jsonElements.jsonElement("object") }
        Then {
            element?.isObject() equals true
            element?.has("title") equals true
        }
    }

    @Test
    fun `return json string when as json string is requested`() {
        val json = """{"object":{"title":"object title"}}"""
        Given { `json elements from`(json) }
        Then { jsonElements.asJsonString() equals json }
    }

    @Test
    fun `return json element as array when requested`() {
        var elements: List<JsonElements>? = null
        Given { `json elements from`("""[{"title":"first title"},{"description":"second description"}]""") }
        When { elements = jsonElements.asArray() }
        Then {
            elements!![0].has("title") equals true
            elements!![1].has("description") equals true
        }
    }

    @Test
    fun `return true when json element contains non-null key`() {
        Given { `json elements from`("""{"title":"article"}""") }
        Then { jsonElements.hasNonNull("title") equals true }
    }

    @Test
    fun `return false when json element has null key`() {
        Given { `json elements from`("""{"title":null}""") }
        Then { jsonElements.hasNonNull("title") equals false }
    }

    @Test
    fun `return false when json element doesn't have key`() {
        Given { `json elements from`("{}") }
        Then { jsonElements.hasNonNull("title") equals false }
    }

    @Test
    fun `return true when json element is null`() {
        Given { `json elements from`("") }
        Then { jsonElements.isNull() equals true }
    }

    @Test
    fun `return false when json element is not null`() {
        Given { `json elements from`("{}") }
        Then { jsonElements.isNull() equals false }
    }

    @Test
    fun `return value as string for given key when string is requested`() {
        Given { `json elements from`("""{"title":"Article title"}""") }
        Then { jsonElements.stringFor("title") equals "Article title" }
    }

    @Test
    fun `return null for given key when string is requested and key is not provided`() {
        Given { `json elements from`("{}") }
        Then { jsonElements.stringFor("title") equals null }
    }

    @Test
    fun `return true when json element is object and check if it's object is requested`() {
        Given { `json elements from`("{}") }
        Then { jsonElements.isObject() equals true }
    }

    @Test
    fun `return false when json element is not object and check if it's object is requested`() {
        Given { `json elements from`("[]") }
        Then { jsonElements.isObject() equals false }
    }

    @Test
    fun `return false when json element is null and check if it's object is requested`() {
        Given { `json elements from`("") }
        Then { jsonElements.isObject() equals false }
    }

    @Test
    fun `return element as array when requested key is array`() {
        Given { `json elements from`("""{"data":[{"id":"12"},{"id":"12"}]}""") }
        Then {
            jsonElements.jsonElement("data").isArray() equals true
            jsonElements.jsonElement("data").asArray().size equals 2
        }
    }

    @Test
    fun `return map with key values when return as map is requested`() {
        Given { `json elements from`("""{"title":"Article title","customObject":{"value":"custom value"}}""") }
        Then {
            jsonElements.asMap() equals mapOf(
                "title" to "Article title",
                "customObject" to mapOf("value" to "custom value"))
        }
    }

    private fun `json elements from`(json: String) {
        jsonElements = `parseJsonToJsonElements`(json)
    }
}