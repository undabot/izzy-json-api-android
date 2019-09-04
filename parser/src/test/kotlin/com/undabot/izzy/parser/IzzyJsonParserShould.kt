package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.CustomObject
import com.undabot.izzy.models.JsonDocument
import org.junit.Test

abstract class IzzyJsonParserShould(val parser: IzzyJsonParser) {

    private lateinit var document: JsonDocument<Any>
    private var result: Any? = null
    private var json: String = ""
    private lateinit var jsonElements: JsonElements

    @Test
    fun `not include top level members in collection serialization when they are null`() {
        Given { document = JsonDocument<List<SerializableDocument>>(links = null, data = null, errors = null, meta = null) }
        When { result = parser.documentCollectionToJson(document as JsonDocument<List<SerializableDocument>>) }
        Then { result equals """{}""" }
    }

    @Test
    fun `not include top level members in serialization when they are null`() {
        Given { document = JsonDocument<SerializableDocument>(links = null, data = null, errors = null, meta = null) }
        When { result = parser.documentToJson(document as JsonDocument<SerializableDocument>) }
        Then { result equals """{}""" }
    }

    @Test
    fun `include data member in collection serialization when it's not null`() {
        Given {
            document = JsonDocument<List<SerializableDocument>>(
                    data = arrayListOf(SerializableDocument(
                            id = "10",
                            type = "articles",
                            attributes = mapOf("attributeKey" to "attributeValue"),
                            relationships = mapOf("relationshipName" to DataWrapper(CustomObject("name"))))))
        }
        When { result = parser.documentCollectionToJson(document as JsonDocument<List<SerializableDocument>>) }
        Then { result equals """{"data":[{"id":"10","type":"articles","attributes":{"attributeKey":"attributeValue"},"relationships":{"relationshipName":{"data":{"value":"name"}}}}]}""" }
    }

    @Test
    fun `include data member in serialization when it's not null`() {
        Given {
            document = JsonDocument(
                    data = SerializableDocument(
                            id = "10",
                            type = "articles",
                            attributes = mapOf("attributeKey" to "attributeValue"),
                            relationships = mapOf("relationshipName" to DataWrapper(CustomObject("name")))))
        }
        When { result = parser.documentToJson(document as JsonDocument<SerializableDocument>) }
        Then { result equals """{"data":{"id":"10","type":"articles","attributes":{"attributeKey":"attributeValue"},"relationships":{"relationshipName":{"data":{"value":"name"}}}}}""" }
    }

    @Test
    fun `parse given json object string to proper object`() {
        var article: Article? = null
        Given { json = """{"title":"Article title","description":"Article description","customObject":{"value":"Custom object value"},"keywords":["key1","key2"]}""" }
        When { article = parser.parse(json, Article::class.java) }
        Then {
            article equals Article().apply {
                title = "Article title"
                description = "Article description"
                customObject = CustomObject("Custom object value")
                keywords = arrayListOf("key1", "key2")
            }
        }
    }

    @Test
    fun `parse given json object as json elements object`() {
        Given { json = """{"title":"Article title","customObject":{"value":"custom object value"}}""" }
        When { jsonElements = parser.parseToJsonElements(json) }
        Then {
            assert(jsonElements.isObject())
            assert(jsonElements.hasNonNull("title"))
            assert(jsonElements.jsonElement("customObject").hasNonNull("value"))
        }
    }

    @Test
    fun `parse given json array as json elements array`() {
        Given {
            json = """
                [{"title":"Article title 1"},
            {"title":"Article title 2","customObject":{"value":"custom object value"}}]
            """.trimMargin()
        }
        When { jsonElements = parser.parseToJsonElements(json) }
        Then {
            assert(jsonElements.isArray())
            val firstElement = jsonElements.asArray()[0]
            val secondElement = jsonElements.asArray()[1]

            assert(firstElement.hasNonNull("title"))

            assert(secondElement.hasNonNull("title"))
            assert(secondElement.jsonElement("customObject").hasNonNull("value"))
        }
    }
}