package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.exceptions.InvalidJsonDocumentException
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.models.Error
import com.undabot.izzy.models.Errors
import com.undabot.izzy.models.Source
import org.junit.Test

class DeserializeErrorsShould {

    private val deserialize = DeserializeErrors()
    private lateinit var documentElements: JsonElements
    private lateinit var actualErrors: Errors

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when 'errors' element is null`() {
        Given { `json document as`("""{"errors":null}""") }
        When { `deserialize is requested`() }
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when 'errors' element is not array`() {
        Given { `json document as`("""{"errors": {}}""") }
        When { `deserialize is requested`() }
    }

    @Test
    fun `return empty array when errors list is empty`() {
        Given { `json document as`("""{"errors":[]}""") }
        When { `deserialize is requested`() }
        Then { actualErrors equals Errors() }
    }

    @Test
    fun `return array of errors when errors list is not empty`() {
        Given {
            `json document as`("""{"errors":[
                                        {"detail":"error detail", "code":"3"},
                                        {"source":{"parameter":"parameter"},"title": "error title"},
                                        {"meta":{"some_key":"some value"}, "status":"400"},
                                        {"custom_properties":{"custom_key":"some value"}}
                                    ]}""")
        }
        When { `deserialize is requested`() }
        Then { actualErrors equals expectedErrors() }
    }

    private fun expectedErrors() = Errors(
            arrayListOf(
                    Error(detail = "error detail", code = "3"),
                    Error(source = Source(parameter = "parameter"), title = "error title"),
                    Error(meta = hashMapOf("some_key" to "some value"), status = "400"),
                    Error(customProperties = hashMapOf("custom_properties" to hashMapOf("custom_key" to "some value"))))
    )

    private fun `deserialize is requested`() {
        actualErrors = deserialize.from(documentElements)
    }

    private fun `json document as`(json: String) {
        documentElements = JacksonParser().parseToJsonElements(json)
    }
}