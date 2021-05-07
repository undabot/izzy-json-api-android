package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.equals
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.Person
import com.undabot.izzy.models.ClassInstance
import com.undabot.izzy.models.DataPool
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.RelationshipFields
import com.undabot.izzy.models.Resource
import com.undabot.izzy.models.ResourceID
import org.junit.Test

class RelationshipMatcherShould {

    private val matcher = RelationshipMatcher()
    private var pool: DataPool = DataPool()

    private val relationshipNameAuthor = "author"
    private val relationshipNameCoauthors = "coauthors"
    private val author = Person().apply {
        id = "1"
        name = "Author name"
    }

    private val relationshipItemList = listOf(
        Person().apply {
            id = "2"
            name = "First coauthor"
        },
        Person().apply {
            id = "3"
            name = "Second coauthor"
        }
    )

    private var itemWithOneRelationship = Article().apply {
        id = "1"
        author = Person().apply { id = "1" }
    }

    private var itemWithOneToManyRelationship = Article().apply {
        id = "1"
        coauthors = arrayListOf(
            Person().apply { id = "2" },
            Person().apply { id = "3" }
        )
    }

    @Test
    fun `match item in pool to form one to one relationship`() {
        Given { pool = `pool with a single relationship`() }
        When { matcher.match(pool) }
        Then { itemWithOneRelationship.author equals author }
    }

    @Test
    fun `match collection items in pool to form one to many relationship`() {
        Given { pool = `pool with a relationship list`() }
        When { matcher.match(pool) }
        Then { assert(itemWithOneToManyRelationship.coauthors == relationshipItemList) }
    }

    private fun fieldWithType(name: String, resource: IzzyResource) = resource.javaClass.declaredFields
        .filter { it.isAnnotationPresent(Relationship::class.java) }
        .filter { it.getAnnotation(Relationship::class.java).name == name }
        .map {
            it.isAccessible = true
            it
        }
        .first()!!

    private fun `pool with a single relationship`(): DataPool =
        DataPool().apply {
            put(
                ResourceID("1", "articles"),
                Resource(
                    ClassInstance(Article::class.java, itemWithOneRelationship),
                    RelationshipFields().apply {
                        add(
                            ResourceID("1", relationshipNameAuthor),
                            fieldWithType(relationshipNameAuthor, itemWithOneRelationship)
                        )
                    }
                )
            )
            put(
                ResourceID("1", relationshipNameAuthor),
                Resource(ClassInstance(Person::class.java, author), RelationshipFields())
            )
        }

    private fun `pool with a relationship list`(): DataPool =
        DataPool().apply {
            put(
                ResourceID("1", "articles"),
                Resource(
                    ClassInstance(Article::class.java, itemWithOneToManyRelationship),
                    RelationshipFields().apply {
                        add(
                            ResourceID("2", relationshipNameCoauthors),
                            fieldWithType(relationshipNameCoauthors, itemWithOneToManyRelationship)
                        )
                        add(
                            ResourceID("3", relationshipNameCoauthors),
                            fieldWithType(relationshipNameCoauthors, itemWithOneToManyRelationship)
                        )
                    }
                )
            )
            relationshipItemList.forEach {
                put(
                    ResourceID(it.id!!, relationshipNameCoauthors),
                    Resource(ClassInstance(Person::class.java, it), RelationshipFields())
                )
            }
        }
}
