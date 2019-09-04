package com.undabot.izzy.retrofit

import com.fasterxml.jackson.core.type.TypeReference
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.CustomObject
import com.undabot.izzy.models.JsonDocument
import com.undabot.izzy.parser.Izzy
import com.undabot.izzy.rawType
import org.junit.Test
import java.lang.reflect.Type

class IzzyRetrofitConverterShould {

    private val converter = IzzyRetrofitConverter(Izzy(JacksonParser()))

    @Test
    fun `return a normal response body converter for normal objects`() {
        val result = converter.responseBodyConverter(documentResourceType(), null, null)
        assert(result is IzzyResponseBodyConverter<*>)
    }

    @Test
    fun `return collection response converter for collections`() {
        val result = converter.responseBodyConverter(documentCollectionType(), null, null)
        assert(result is IzzyCollectionResponseBodyConverter<*>)
    }

    @Test
    fun `return a request body converter for resource`() {
        val result = converter.requestBodyConverter(resourceType(), null, null, null)
        assert(result is IzzyRequestBodyConverter<*>)
    }

    @Test
    fun `return a request body converter for resource collection`() {
        val result = converter.requestBodyConverter(resourceCollectionType(), null, null, null)
        assert(result is IzzyCollectionRequestBodyConverter<*>)
    }

    @Test
    fun `return null on retrofit string converter`() {
        assert(converter.stringConverter(documentResourceType(), null, null) == null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throw exception when response body converter is requested with unsupported type`() {
        converter.responseBodyConverter(CustomObject::class.java, null, null)
    }

    private fun resourceCollectionType(): Type = emptyList<Article>()::class.java.rawType

    private fun resourceType(): Type = Article::class.java.rawType

    private fun documentCollectionType(): Type = object : TypeReference<JsonDocument<List<Article>>>() {}.type

    private fun documentResourceType(): Type = object : TypeReference<JsonDocument<Article>>() {}.type
}