package com.undabot.izzy.retrofit

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.Person
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.parser.Izzy
import com.undabot.izzy.parser.IzzyConfiguration
import okhttp3.MediaType
import okhttp3.RequestBody
import org.junit.Before
import org.junit.Test

class IzzyCollectionRequestBodyConverterShould {

    private lateinit var requestConverter: IzzyCollectionRequestBodyConverter<IzzyResource>

    private var izzy: Izzy = Izzy(JacksonParser(IzzyConfiguration(arrayOf(Article::class.java, Person::class.java))))
    private val bodyString = """{"data":[{"type":"articles","attributes":{"title":"Article title"}}]}"""
    private val expectedBody = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON_API), bodyString)
    private lateinit var actualBody: RequestBody
    private var resources: List<Article> = arrayListOf(Article("Article title"))

    @Before
    fun prepare() {
        requestConverter = IzzyCollectionRequestBodyConverter(izzy)
    }

    @Test
    fun `return request body for given document`() {
        Given { resources }
        When { actualBody = requestConverter.convert(resources) }
        Then {
            actualBody.contentLength() equals expectedBody.contentLength()
            actualBody.contentType() equals expectedBody.contentType()
        }
    }
}