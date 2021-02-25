package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.jackson.JacksonParser
import org.junit.Test

class DeserializeErrorCustomPropertiesShould {

    private val deserializeErrorCustomProperties = DeserializeErrorCustomProperties()
    private lateinit var errorElement: JsonElements

    private val idKey = "id"
    private val statusKey = "status"
    private val codeKey = "code"
    private val titleKey = "title"
    private val detailKey = "detail"
    private val sourceKey = "source"
    private val customKey = "customError"
    private val customValue = "customErrorValue"
    private val expectedErrorProperties =
        listOf(idKey, statusKey, codeKey, titleKey, detailKey, sourceKey, META)
    private val errorJson =
        """{"customError":"customErrorValue","id":"12","status":"error","code":"code","title":"title","detail":"details"}"""

    private var result: Map<String, Any?>? = null

    @Test
    fun `deserialize custom error properties but ignore expected`() {
        Given { `json error element as`(errorJson) }
        When {
            result = deserializeErrorCustomProperties.from(errorElement, expectedErrorProperties)
        }
        Then {
            result equals mapOf(customKey to customValue)
        }
    }

    private fun `json error element as`(json: String) {
        errorElement = JacksonParser().parseToJsonElements(json)
    }
}