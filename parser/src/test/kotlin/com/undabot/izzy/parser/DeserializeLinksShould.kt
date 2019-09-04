package com.undabot.izzy.parser

import com.undabot.izzy.FULL_LINKS
import com.undabot.izzy.Given
import com.undabot.izzy.LINKS_WITH_NULL_VALUES
import com.undabot.izzy.NULL_LINKS
import com.undabot.izzy.Then
import com.undabot.izzy.WITHOUT_LINKS
import com.undabot.izzy.When
import com.undabot.izzy.asResource
import com.undabot.izzy.equals
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.models.Link
import com.undabot.izzy.models.Links
import org.junit.Before
import org.junit.Test

class DeserializeLinksShould {

    private lateinit var deserializeLinks: DeserializeLinks
    private var actualLinks: Links? = null
    private lateinit var linksJson: JsonElements

    @Before
    fun prepare() {
        deserializeLinks = DeserializeLinks()
    }

    @Test
    fun `return null object when links object is not included`() {
        Given { `json elements from`(WITHOUT_LINKS) }
        When { `deserialize is requested`() }
        Then { actualLinks equals null }
    }

    @Test
    fun `return null object when links object is null`() {
        Given { `json elements from`(NULL_LINKS) }
        When { `deserialize is requested`() }
        Then { actualLinks equals null }
    }

    @Test
    fun `return null self link when self is not present`() {
        Given { `json elements from`(LINKS_WITH_NULL_VALUES) }
        When { `deserialize is requested`() }
        Then { actualLinks?.self equals null }
    }

    @Test
    fun `return null first link when first is not present`() {
        Given { `json elements from`(LINKS_WITH_NULL_VALUES) }
        When { `deserialize is requested`() }
        Then { actualLinks?.first equals null }
    }

    @Test
    fun `return null last link when last is not present`() {
        Given { `json elements from`(LINKS_WITH_NULL_VALUES) }
        When { `deserialize is requested`() }
        Then { actualLinks?.last equals null }
    }

    @Test
    fun `return null prev link when prev is not present`() {
        Given { `json elements from`(LINKS_WITH_NULL_VALUES) }
        When { `deserialize is requested`() }
        Then { actualLinks?.prev equals null }
    }

    @Test
    fun `return null next link when next is not present`() {
        Given { `json elements from`(LINKS_WITH_NULL_VALUES) }
        When { `deserialize is requested`() }
        Then { actualLinks?.next equals null }
    }

    @Test
    fun `return null related link when related is not present`() {
        Given { `json elements from`(LINKS_WITH_NULL_VALUES) }
        When { `deserialize is requested`() }
        Then { actualLinks?.related equals null }
    }

    @Test
    fun `return fully parsed links`() {
        Given { `json elements from`(FULL_LINKS) }
        When { `deserialize is requested`() }
        Then { actualLinks equals expectedFullLinks() }
    }

    private fun expectedFullLinks() = Links(
            self = Link("self-url"),
            first = Link("first-url"),
            last = Link("last-url"),
            prev = Link("prev-url"),
            next = Link("next-url"),
            related = Link(
                    href = "related-url",
                    meta = mapOf("related" to "meta",
                            "key" to "value")
            )
    )

    private fun `deserialize is requested`() {
        actualLinks = deserializeLinks.from(linksJson)
    }

    private fun `json elements from`(json: String) {
        linksJson = JacksonParser().parseToJsonElements(json.asResource())
    }
}