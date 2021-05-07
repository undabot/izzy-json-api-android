package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.equals
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.Person
import com.undabot.izzy.model.Shield
import com.undabot.izzy.model.Weapon
import com.undabot.izzy.models.ResourceID
import com.undabot.izzy.parser.DataWrapper.Companion.NULLABLE_FIELD
import org.junit.Test

class MapResourcetoSerializableDocumentShould {

    private val mapper = ResourceToSerializableDocumentMapper(RelationshipFieldMapper())
    private val articleTitle = "Title of the article"
    private val articleDescription = "Description of the article"
    private val testPersonName = "testPerson"
    private val secondPersonName = "testPerson2"
    private val testId = "test"
    private val testIdSecond = "test2"
    private val testIdArticle = "testArticleId"
    private val testPerson = Person(testPersonName).apply { id = testId }
    private val secondPerson = Person(secondPersonName).apply { id = testIdSecond }
    private var result: SerializableDocument? = null
    private val personWithoutAttributes = Person().apply { id = testId }
    private val personWithoutId = Person()

    private val articleWithAuthors = Article("testArticle")
        .apply {
            id = testIdArticle
            title = articleTitle
            description = articleDescription
            author = testPerson
            coauthors = listOf(
                testPerson,
                secondPerson
            )
        }

    private val personWithArticle = Person(testPersonName)
        .apply {
            id = testId
            favoriteArticle = articleWithAuthors
        }

    @Test
    fun `extract data from a resource with a single relationship`() {
        Given { personWithArticle }
        When { result = mapper.mapFrom(personWithArticle) }
        Then { assert(result == `serializable document of person with favorite article`()) }
    }

    @Test
    fun `extract data from given resource data with collection relationship`() {
        Given { articleWithAuthors }
        When { result = mapper.mapFrom(articleWithAuthors) }
        Then { assert(result == `serializable document of article with coauthors`()) }
    }

    @Test
    fun `set relationships to null when there are no relationships`() {
        Given { testPerson }
        When { result = mapper.mapFrom(testPerson) }
        Then { result?.relationships equals null }
    }

    @Test
    fun `set attributes to null when there are no attributes provided`() {
        Given { personWithoutAttributes }
        When { result = mapper.mapFrom(personWithoutAttributes) }
        Then { result?.attributes equals null }
    }

    @Test
    fun `set id to null when id is not provided`() {
        Given { personWithoutId }
        When { result = mapper.mapFrom(personWithoutId) }
        Then { result?.id equals null }
    }

    @Test
    fun `set relationships collection data to empty array when relationships are empty`() {
        val article = Article().apply { coauthors = arrayListOf() }

        val result = mapper.mapFrom(article)
        Then { result.relationships equals mapOf("coauthors" to DataWrapper(emptyList<Any>())) }
    }

    @Test
    fun `set relationship data to null when relationship is annotated as nullable`() {
        val weapon: Weapon = Weapon().apply { shield = null }

        val result = mapper.mapFrom(weapon)
        Then { result.relationships equals mapOf("shield" to DataWrapper(NULLABLE_FIELD)) }
    }

    @Test
    fun `set attribute value to null when attribute field is annotated as nullable`() {
        val shield = Shield()

        val result = mapper.mapFrom(shield)

        Then { result.attributes equals mapOf("strength" to NULLABLE_FIELD) }
    }

    private fun `serializable document of article with coauthors`(): SerializableDocument {
        return SerializableDocument(
            testIdArticle,
            "articles",
            mapOf("title" to articleTitle, "description" to articleDescription),
            mapOf(
                "author" to DataWrapper(ResourceID(testId, "persons")),
                "coauthors" to DataWrapper(
                    arrayListOf(
                        ResourceID(testId, "persons"),
                        ResourceID(testIdSecond, "persons")
                    )
                )
            )
        )
    }

    private fun `serializable document of person with favorite article`(): SerializableDocument {
        return SerializableDocument(
            testId, "persons", mapOf("name" to testPersonName),
            mapOf("favoriteArticle" to DataWrapper(ResourceID(testIdArticle, "articles")))
        )
    }
}
