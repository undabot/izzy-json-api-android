package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.models.Link
import org.junit.Before
import org.junit.Test

class DeserializeLinkShould {

    private lateinit var deserialize: DeserializeLink
    private lateinit var linkElement: JsonElements
    private var actualLink: Link? = null

    @Before
    fun prepare() {
        deserialize = DeserializeLink()
    }

    @Test
    fun `return null link when given element is null`() {
        Given { `link element from`("""{"link":null}""") }
        When { `deserialize is requested`() }
        Then { actualLink equals null }
    }

    @Test
    fun `return link with href only when element is string`() {
        Given { `link element from`("""{"link":"url"}""") }
        When { `deserialize is requested`() }
        Then { actualLink equals Link(href = "url") }
    }

    @Test
    fun `return link with href when element is object`() {
        Given { `link element from`("""{"link":{"href":"url"}}""") }
        When { `deserialize is requested`() }
        Then { actualLink equals Link(href = "url") }
    }

    @Test
    fun `return link with meta when element is object`() {
        Given { `link element from`("""{"link":{"meta":{"key":"value"}}}""") }
        When { `deserialize is requested`() }
        Then { actualLink equals Link(meta = mapOf("key" to "value")) }
    }

    @Test
    fun `return link with null meta when meta is null`() {
        Given { `link element from`("""{"link":{"meta":null}}""") }
        When { `deserialize is requested`() }
        Then { actualLink equals Link() }
    }

    private fun `link element from`(json: String) {
        linkElement = JacksonParser().parseToJsonElements(json).jsonElement("link")
    }

    private fun `deserialize is requested`() {
        actualLink = deserialize.from(linkElement)
    }
}
