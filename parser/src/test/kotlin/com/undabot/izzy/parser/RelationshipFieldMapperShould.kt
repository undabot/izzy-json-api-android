package com.undabot.izzy.parser

import com.undabot.izzy.And
import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.Person
import com.undabot.izzy.model.Weapon
import com.undabot.izzy.models.ResourceID
import com.undabot.izzy.nonNullFields
import com.undabot.izzy.parser.DataWrapper.Companion.NULLABLE_FIELD
import org.junit.Test

class RelationshipFieldMapperShould {

    private var result: Set<Pair<String, Any?>>? = null

    private val testArticleName = "testArticle"
    private val testPersonName = "testPerson"
    private val secondPersonName = "testPerson2"
    private val testId = "test"
    private val testIdSecond = "test2"
    private val testPerson = Person(testPersonName)
    private val secondPerson = Person(secondPersonName)

    private val favArticle = Article(testArticleName).apply { id = testId }
    private val personWithArticle = Person(testPersonName).apply {
        id = testId
        favoriteArticle = favArticle
    }

    private val articleWithAuthors = Article("testArticle")
        .apply {
            id = testId
            coauthors = listOf(
                testPerson.apply { id = testId },
                secondPerson.apply { id = testIdSecond }
            )
        }

    private var mapper: RelationshipFieldMapper? = RelationshipFieldMapper()

    @Test
    fun `map relationship from a resource to a relationship item`() {
        Given { personWithArticle }
        When {
            result = personWithArticle.nonNullFields()
                .groupBy { it.first.isAnnotationPresent(Relationship::class.java) }[true]
                .let { mapper!!.map(it!!) }
        }
        Then {
            And { assert(result!!.contains(Pair("favoriteArticle", DataWrapper(ResourceID(testId, "articles"))))) }
        }
    }

    @Test
    fun `map relationship from a resource collection to a relationship collection`() {
        Given { articleWithAuthors }
        When {
            result = articleWithAuthors.nonNullFields()
                .groupBy { it.first.isAnnotationPresent(Relationship::class.java) }[true]
                .let { mapper!!.map(it!!) }
        }
        Then {
            And {
                assert(
                    result!!.contains(
                        Pair(
                            "coauthors",
                            DataWrapper(
                                arrayListOf(
                                    ResourceID(testId, "persons"),
                                    ResourceID(testIdSecond, "persons")
                                )
                            )
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `map empty relationships collection to a empty collection`() {
        val article = Article().apply { coauthors = arrayListOf() }

        val result = article.nonNullFields()
            .groupBy { it.first.isAnnotationPresent(Relationship::class.java) }[true]
            .let { mapper!!.map(it!!) }

        Then { assert(result.contains("coauthors" to DataWrapper(arrayListOf<Article>()))) }
    }

    @Test
    fun `map nullable relationship with null data`() {
        val weapon: Weapon = Weapon().apply { shield = null }

        val result = weapon.nonNullFields()
            .groupBy { it.first.isAnnotationPresent(Relationship::class.java) }[true]
            .let { mapper!!.map(it!!) }
        Then { assert(result.contains("shield" to DataWrapper(NULLABLE_FIELD))) }
    }
}
