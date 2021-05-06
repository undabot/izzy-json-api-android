package com.undabot.izzy.models

import com.undabot.izzy.COLLECTION_ARTICLES
import com.undabot.izzy.Given
import com.undabot.izzy.SINGLE_ARTICLE
import com.undabot.izzy.Then
import com.undabot.izzy.When
import com.undabot.izzy.annotatedWith
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.asResource
import com.undabot.izzy.isCollection
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.Person
import com.undabot.izzy.parser.DATA
import com.undabot.izzy.parser.JsonElements
import org.junit.Before
import org.junit.Test

class RelationshipFieldsShould {

    private var fields = RelationshipFields()
    private val person = Person("name")

    @Before
    fun prepare() {
        fields = RelationshipFields()
    }

    @Test
    fun `add objects and retrieve them in same order`() {
        Given {
            fields
        }
        When {
            fields.add(ResourceID("1", "first"), (person::class.java.fields[0]))
            fields.add(ResourceID("2", "second"), (person::class.java.fields[0]))
        }
        Then {
            assert(fields.get().toList()[0].first.id == "1")
            assert(fields.get().toList()[1].first.id == "2")
        }
    }

    @Test
    fun `add to pool from array`() {
        lateinit var elements: JsonElements
        Given {
            elements = JacksonParser().parseToJsonElements(COLLECTION_ARTICLES.asResource())
        }
        When {
            fields.addToPool(elements.jsonElement(DATA), Article::class.java.annotatedWith(Relationship::class.java).first())
        }
        Then {
            assert(fields.get().size == 2)
        }
    }

    @Test
    fun `add to pool from an object`() {
        lateinit var elements: JsonElements
        Given {
            elements = JacksonParser().parseToJsonElements(SINGLE_ARTICLE.asResource())
        }
        When {
            fields.addToPool(
                elements.jsonElement(DATA),
                Article::class.java.annotatedWith(Relationship::class.java)
                    .first { !it.type.isCollection() }
            )
        }
        Then {
            assert(fields.get().size == 1)
        }
    }
}
