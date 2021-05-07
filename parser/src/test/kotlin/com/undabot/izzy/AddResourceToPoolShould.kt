package com.undabot.izzy

import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.Person
import com.undabot.izzy.models.DataPool
import com.undabot.izzy.models.ResourceID
import com.undabot.izzy.parser.Izzy
import com.undabot.izzy.parser.IzzyConfiguration
import org.junit.Before
import org.junit.Test

class AddResourceToPoolShould {

    private var pool: DataPool = DataPool()
    private var addResourceToPool: AddResourceToPool = AddResourceToPool(pool)
    private var testId = "10"
    private var testResource: Article = Article().apply {
        id = testId
        author = Person(name = "").apply { id = testId }
    }

    private var relationshipJSON = JacksonParser(IzzyConfiguration()).parseToJsonElements(
        """{
                      "author":{
                        "data": {
                          "type": "persons",
                          "id": "10"
                        }
                      }
                    }"""
    )

    private var type = "articles"
    private var parser = JacksonParser(IzzyConfiguration(arrayOf(Article::class.java, Person::class.java)))
    private var izzy = Izzy(parser)

    @Before
    fun prepare() {
        pool = DataPool()
        addResourceToPool = AddResourceToPool(pool)
        izzy = Izzy(parser)
    }

    @Test
    fun `put resource into pool with relationships`() {
        Given { testResource }
        When {
            addResourceToPool.resourceWithRelationships(testResource, relationshipJSON, testResource.typeFromResource())
        }
        Then {
            `class from pool`() equals Article::class.java
            `class of relationship from pool`() equals Person::class.java
        }
    }

    private fun `class of relationship from pool`() =
        pool.resourceForId(ResourceID(testId, type))!!.second.fieldOrNull(ResourceID("10", "persons"))!!.second.type

    private fun `class from pool`() = pool.resourceForId(ResourceID(testId, type))!!.first.classData
}
