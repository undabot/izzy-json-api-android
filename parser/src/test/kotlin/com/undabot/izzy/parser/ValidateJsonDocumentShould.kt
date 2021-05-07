package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.When
import com.undabot.izzy.exceptions.InvalidJsonDocumentException
import com.undabot.izzy.jackson.JacksonParser
import org.junit.Test

class ValidateJsonDocumentShould {

    private val validate = ValidateJsonDocument()
    private lateinit var givenElements: JsonElements
    private val parser = JacksonParser()

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when root is not JSON object`() {
        Given { `json document`("""[]""") }
        When { `validation is requested`() }
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when both 'errors' and 'data' are included`() {
        Given { `json document`("""{"data":[], "errors":[]}""") }
        When { `validation is requested`() }
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when none of required top-level members is not provided`() {
        Given { `json document`("""{"jsonapi":{}}""") }
        When { `validation is requested`() }
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when 'data' member is not provided and 'included' is provided`() {
        Given { `json document`("""{"errors":[], "included":[]}""") }
        When { `validation is requested`() }
    }

    private fun `json document`(json: String) {
        givenElements = parser.parseToJsonElements(json)
    }

    private fun `validation is requested`() {
        validate.from(givenElements)
    }
}
