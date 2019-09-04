package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.models.Source
import org.junit.Test

class DeserializeSourceShould {

    private val deserializeSource = DeserializeSource()
    private lateinit var sourceElements: JsonElements
    private var actualSource: Source? = null

    @Test
    fun `return null when source is not provided`() {
        Given { `source element from`("""{"error":{}}""") }
        When { `deserialize is requested`() }
        Then { actualSource equals null }
    }

    @Test
    fun `return null 'pointer' from element to null 'pointer' in source`() {
        Given { `source element from`("""{"error":{"source":{}}}""") }
        When { `deserialize is requested`() }
        Then { actualSource?.pointer equals null }
    }

    @Test
    fun `return 'pointer' from element to 'pointer' in source`() {
        Given { `source element from`("""{"error":{"source":{"pointer":"source pointer"}}}""") }
        When { `deserialize is requested`() }
        Then { actualSource?.pointer equals "source pointer" }
    }

    @Test
    fun `return null 'parameter' from element to null 'parameter' in source`() {
        Given { `source element from`("""{"error":{"source":{}}}""") }
        When { `deserialize is requested`() }
        Then { actualSource?.parameter equals null }
    }

    @Test
    fun `return 'parameter' from element to 'parameter' in source`() {
        Given { `source element from`("""{"error":{"source":{"parameter":"source parameter"}}}""") }
        When { `deserialize is requested`() }
        Then { actualSource?.parameter equals "source parameter" }
    }

    private fun `source element from`(json: String) {
        sourceElements = JacksonParser().parseToJsonElements(json).jsonElement("source")
    }

    private fun `deserialize is requested`() {
        actualSource = deserializeSource.from(sourceElements)
    }
}