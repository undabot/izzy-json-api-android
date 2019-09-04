package com.undabot.izzy.retrofit

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.Person
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument
import com.undabot.izzy.parser.Izzy
import com.undabot.izzy.parser.IzzyConfiguration
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test

class IzzyResponseBodyConverterShould {

    private lateinit var responseBodyConverter: IzzyResponseBodyConverter<IzzyResource>

    private lateinit var responseBody: ResponseBody
    private var izzy: Izzy = Izzy(JacksonParser(IzzyConfiguration(arrayOf(Article::class.java, Person::class.java))))
    private var expectedDocument: JsonDocument<IzzyResource> =
            JsonDocument(data = Article("Article title").apply { id = "20" })
    private lateinit var actualDocument: JsonDocument<IzzyResource>

    @Before
    fun prepare() {
        responseBody = ResponseBody.create(null,
                """{"data":{"id":"20","type":"articles","attributes":{"title":"Article title"}}}""")
        responseBodyConverter = IzzyResponseBodyConverter(izzy)
    }

    @Test
    fun `return deserialize response body to document json document with collection`() {
        Given { responseBody }
        When { actualDocument = responseBodyConverter.convert(responseBody) }
        Then { actualDocument equals expectedDocument }
    }
}