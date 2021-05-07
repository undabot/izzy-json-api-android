package com.undabot.izzy.parser

import com.undabot.izzy.COLLECTION_ARTICLES
import com.undabot.izzy.COLLECTION_ARTICLE_WITHOUT_INCLUDED
import com.undabot.izzy.COLLECTION_ARTICLE_WITH_COAUTHORS_META
import com.undabot.izzy.COLLECTION_ARTICLE_WITH_LINKS
import com.undabot.izzy.COLLECTION_ARTICLE_WITH_UNKNOWN_RESOURCE
import com.undabot.izzy.COLLECTION_OF_ARTICLES_ARTICLE_WITHOUT_INCLUDED_AND_META
import com.undabot.izzy.COMPLEX_SINGLE_RESOURCE_DOCUMENT
import com.undabot.izzy.DOCUMENT_COLLECTION_WITH_LINKS
import com.undabot.izzy.DOCUMENT_COLLECTION_WITH_META
import com.undabot.izzy.DOCUMENT_WITHOUT_TOP_LEVEL_MEMBERS
import com.undabot.izzy.DOCUMENT_WITH_DATA_AND_ERRORS
import com.undabot.izzy.DOCUMENT_WITH_ERRORS
import com.undabot.izzy.DOCUMENT_WITH_INCLUDED_AND_MISSING_DATA
import com.undabot.izzy.DOCUMENT_WITH_LINKS
import com.undabot.izzy.DOCUMENT_WITH_META
import com.undabot.izzy.INVALID_ROOT_ELEMENT
import com.undabot.izzy.PERSON_WITHOUT_ATTRIBUTES
import com.undabot.izzy.PERSON_WITH_ARTICLE
import com.undabot.izzy.SINGLE_ARTICLE
import com.undabot.izzy.SINGLE_ARTICLE_WITHOUT_INCLUDED
import com.undabot.izzy.SINGLE_ARTICLE_WITHOUT_INCLUDED_AND_META
import com.undabot.izzy.SINGLE_ARTICLE_WITH_COAUTHORS_META
import com.undabot.izzy.SINGLE_ARTICLE_WITH_LINKS
import com.undabot.izzy.SINGLE_ARTICLE_WITH_UNKNOWN_RESOURCE
import com.undabot.izzy.SINGLE_POLYMORPHIC
import com.undabot.izzy.Then
import com.undabot.izzy.asResource
import com.undabot.izzy.equals
import com.undabot.izzy.exceptions.InvalidJsonDocumentException
import com.undabot.izzy.jackson.JacksonParser
import com.undabot.izzy.model.Article
import com.undabot.izzy.model.CustomObject
import com.undabot.izzy.model.Person
import com.undabot.izzy.model.Player
import com.undabot.izzy.model.Shield
import com.undabot.izzy.model.Weapon
import com.undabot.izzy.models.Error
import com.undabot.izzy.models.Errors
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.Link
import com.undabot.izzy.models.Links
import com.undabot.izzy.models.Source
import org.junit.Before
import org.junit.Test

class IzzyShould {

    private val article1 = Article().apply {
        id = "1"
        title = "Title"
        description = "Desc"
        customObject = CustomObject("Hello")
        keywords = arrayListOf("key1", "key2")
    }

    private val article2 = Article().apply {
        id = "2"
        title = "Title"
        description = "Desc"
        customObject = CustomObject("Hello")
        keywords = arrayListOf("key2", "key1")
    }

    private val person1: Person = Person().apply {
        id = "1"
        name = "Pero"
    }

    private val person2: Person = Person().apply {
        id = "2"
        name = "Kanta"
    }

    private val person3: Person = Person().apply {
        id = "3"
        name = "Metlica"
    }

    @Before
    fun prepare() {
        article1.coauthors = arrayListOf(person2, person3)
        article1.author = person1

        article2.author = person3
        article2.coauthors = arrayListOf(person1)

        person1.apply {
            favoriteArticle = article1
            supervisor = person2
        }
        person2.favoriteArticle = article1
        person3.favoriteArticle = article1
    }

    @Test
    fun `deserialize json with one resource to json document`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(SINGLE_ARTICLE.asResource())

        Then {
            result.data equals article1
            result.data?.author equals person1
            result.data?.coauthors equals arrayListOf(person2, person3)
            result.data?.author?.favoriteArticle equals article1
            result.data?.author?.supervisor equals person2
        }
    }

    @Test
    fun `deserialize json with collection to json document`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(COLLECTION_ARTICLES.asResource())

        Then {
            result.data equals arrayListOf(article2, article1)
            result.data!![0].author equals person3
            result.data!![1].author equals person1
            result.data!![0].author?.favoriteArticle equals article1
            result.data!![1].author?.favoriteArticle equals article1
            result.data!![0].author?.supervisor equals null
            result.data!![1].author?.supervisor equals person2
        }
    }

    @Test
    fun `deserialize to null object when json contains unknown type`() {
        val result = `izzy with classes`()
            .deserializeToDocument<Article>(SINGLE_ARTICLE.asResource())

        Then { result.data equals null }
    }

    @Test
    fun `deserialize to empty collection when json contains unknown types`() {
        val result = `izzy with classes`()
            .deserializeToCollection<Article>(COLLECTION_ARTICLES.asResource())

        Then { result.data?.isEmpty() equals true }
    }

    @Test
    fun `deserialize to proper type when we declare return type as base Izzy resource`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<IzzyResource>(SINGLE_ARTICLE.asResource())
        Then { assert(result.data is Article) }
    }

    @Test
    fun `deserialize proper relationship objects when relationship is declared as base resource`() {
        val result = `izzy with classes`(Player::class.java, Weapon::class.java, Shield::class.java)
            .deserializeToDocument<Player>(SINGLE_POLYMORPHIC.asResource())

        Then {
            assert(result.data?.items!![0] is Weapon)
            assert(result.data?.items!![1] is Shield)
        }
    }

    @Test
    fun `serialize object with single relationship into valid JSON`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java).serializeItem(person1)
        result equals PERSON_WITH_ARTICLE.asResource()
    }

    @Test
    fun `serialize object with single relationship and a collection relationship into valid JSON`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java).serializeItem(article1)
        result equals SINGLE_ARTICLE_WITHOUT_INCLUDED_AND_META.asResource()
    }

    @Test
    fun `serialize collection of items into valid JSON`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java).serializeItemCollection(listOf(article1, article2))
        result equals COLLECTION_OF_ARTICLES_ARTICLE_WITHOUT_INCLUDED_AND_META.asResource()
    }

    @Test
    fun `deserialize relationships of single object as base objects with id's only when they are not in included `() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(SINGLE_ARTICLE_WITHOUT_INCLUDED.asResource())
        Then {
            result.data?.author equals Person().apply { id = "1" }
            result.data?.author?.id equals "1"
            result.data?.coauthors!![0] equals Person().apply { id = "2" }
            result.data?.coauthors!![0].id equals "2"
            result.data?.coauthors!![1] equals Person().apply { id = "3" }
            result.data?.coauthors!![1].id equals "3"
        }
    }

    @Test
    fun `deserialize relationships of collection as base objects with id's only when they are not in included `() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(COLLECTION_ARTICLE_WITHOUT_INCLUDED.asResource())
        Then {
            result.data!![0].author equals Person().apply { id = "1" }
            result.data!![0].author?.id equals "1"
            result.data!![0].coauthors!![0] equals Person().apply { id = "2" }
            result.data!![0].coauthors!![0].id equals "2"
            result.data!![0].coauthors!![1] equals Person().apply { id = "3" }
            result.data!![0].coauthors!![1].id equals "3"
        }
    }

    @Test
    fun `deserialize single resource document with links`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(DOCUMENT_WITH_LINKS.asResource())
        Then {
            result.links equals `expected links`()
            result.data equals article1
        }
    }

    @Test
    fun `deserialize collection resources document with links`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(DOCUMENT_COLLECTION_WITH_LINKS.asResource())
        Then {
            result.links equals `expected links`()
            result.data equals arrayListOf(article2, article1)
        }
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when root is not JSON object on single resource deserialization`() {
        `izzy with classes`().deserializeToDocument<Article>(INVALID_ROOT_ELEMENT.asResource())
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when both 'errors' and 'data' are included on single resource deserialization`() {
        `izzy with classes`().deserializeToDocument<Article>(DOCUMENT_WITH_DATA_AND_ERRORS.asResource())
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when none of required top-level members is not provided on single resource deserialization`() {
        `izzy with classes`().deserializeToDocument<Article>(DOCUMENT_WITHOUT_TOP_LEVEL_MEMBERS.asResource())
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when 'data' member is not provided and 'included' is provided on single resource deserialization`() {
        `izzy with classes`().deserializeToDocument<Article>(DOCUMENT_WITH_INCLUDED_AND_MISSING_DATA.asResource())
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when root is not JSON object on collections resource deserialization`() {
        `izzy with classes`().deserializeToCollection<Article>(INVALID_ROOT_ELEMENT.asResource())
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when both 'errors' and 'data' are included on collections resource deserialization`() {
        `izzy with classes`().deserializeToCollection<Article>(DOCUMENT_WITH_DATA_AND_ERRORS.asResource())
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when none of required top-level members is not provided on collections resource deserialization`() {
        `izzy with classes`().deserializeToCollection<Article>(DOCUMENT_WITHOUT_TOP_LEVEL_MEMBERS.asResource())
    }

    @Test(expected = InvalidJsonDocumentException::class)
    fun `throw exception when 'data' member is not provided and 'included' is provided on collections resource deserialization`() {
        `izzy with classes`().deserializeToCollection<Article>(DOCUMENT_WITH_INCLUDED_AND_MISSING_DATA.asResource())
    }

    @Test
    fun `ignore unknown relationships in single resource document`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(SINGLE_ARTICLE_WITH_UNKNOWN_RESOURCE.asResource())
        Then {
            result.data equals article1
            result.data?.author equals person1
            result.data?.coauthors!![0] equals person2
            result.data?.coauthors!![1] equals person3
        }
    }

    @Test
    fun `ignore unknown relationships in resource collection document`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(COLLECTION_ARTICLE_WITH_UNKNOWN_RESOURCE.asResource())
        Then {
            result.data!![0] equals article1
            result.data!![0].author equals person1
            result.data!![0].coauthors!![0] equals person2
            result.data!![0].coauthors!![1] equals person3
        }
    }

    @Test
    fun `deserialize errors when document represents error response`() {
        val result = `izzy with classes`()
            .deserializeToDocument<Article>(DOCUMENT_WITH_ERRORS.asResource())
        Then {
            result.errors equals `expected errors`()
            result.data equals null
        }
    }

    @Test
    fun `deserialize errors when document collection represents error response`() {
        val result = `izzy with classes`()
            .deserializeToCollection<Article>(DOCUMENT_WITH_ERRORS.asResource())
        Then {
            result.errors equals `expected errors`()
            result.data equals null
        }
    }

    @Test
    fun `deserialize single resource document with meta`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(DOCUMENT_WITH_META.asResource())
        Then {
            result.meta equals `expected meta`()
            result.data equals article1
        }
    }

    @Test
    fun `deserialize resource collection document with meta`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(DOCUMENT_COLLECTION_WITH_META.asResource())
        Then {
            result.meta equals `expected meta`()
            result.data!![0] equals article1
        }
    }

    @Test
    fun `deserialize complex single resource document`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(COMPLEX_SINGLE_RESOURCE_DOCUMENT.asResource())
        Then {
            result.meta equals `expected meta`()
            result.data equals article1
            result.data?.coauthors equals arrayListOf(Person().apply { id = "2" }, person3)
            result.data?.author equals person1
            result.data?.coauthors!![1].favoriteArticle equals Article().apply { id = "2" }
        }
    }

    @Test
    fun `deserialize meta from top level resource in document with single resource`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(SINGLE_ARTICLE_WITH_COAUTHORS_META.asResource())
        Then {
            result.data equals article1
            result.data!!.meta equals `expected top level meta`()
        }
    }

    @Test
    fun `deserialize meta from relationship resource in document with single resource`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(SINGLE_ARTICLE_WITH_COAUTHORS_META.asResource())
        Then {
            result.data equals article1
            result.data!!.coauthors!![0].meta equals `expected resource meta`()
        }
    }

    @Test
    fun `deserialize meta from top level resources in document with resource collection`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(COLLECTION_ARTICLE_WITH_COAUTHORS_META.asResource())
        Then {
            result.data!![0] equals article1
            result.data!![0].meta equals `expected top level meta`()
        }
    }

    @Test
    fun `deserialize meta from relationship resource in document with resource collection`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(COLLECTION_ARTICLE_WITH_COAUTHORS_META.asResource())
        Then {
            result.data!![0] equals article1
            result.data!![0].coauthors!![0].meta equals `expected resource meta`()
        }
    }

    @Test
    fun `deserialize links from resource in document with single resource`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(SINGLE_ARTICLE_WITH_LINKS.asResource())
        Then {
            result.data equals article1
            result.data?.links equals `expected links`()
        }
    }

    @Test
    fun `deserialize links from resources in document with resource collection`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(COLLECTION_ARTICLE_WITH_LINKS.asResource())
        Then {
            result.data!![0] equals article1
            result.data!![0].links equals `expected links`()
        }
    }

    @Test
    fun `ignore relationship with null data on deserialization`() {

        val result = `izzy with classes`(Weapon::class.java, Shield::class.java)
            .deserializeToDocument<Weapon>(
                """{"data":{"type":"weapons","id":"1","relationships":{"shield":{"data":null}}}}"""
            )

        Then { result.data equals Weapon().apply { id = "1" } }
    }

    @Test
    fun `deserialize links from included resources in document with single resource`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Article>(SINGLE_ARTICLE_WITH_LINKS.asResource())
        Then {
            result.data?.author?.links equals `expected links`()
        }
    }

    @Test
    fun `deserialize resource with null attributes to empty resource`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToDocument<Person>(PERSON_WITHOUT_ATTRIBUTES.asResource())
        Then {
            result.data equals Person().apply {
                id = "1"
                favoriteArticle = Article().apply { id = "1" }
            }
        }
    }

    @Test
    fun `deserialize links from included resources in document with resource collection`() {
        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .deserializeToCollection<Article>(COLLECTION_ARTICLE_WITH_LINKS.asResource())
        Then {
            result.data!![0].author?.links equals `expected links`()
        }
    }

    @Test
    fun `deserialize and serialize back single resource document`() {
        val documentJson = PERSON_WITH_ARTICLE.asResource()
        val parser = `izzy with classes`(Article::class.java, Person::class.java)
        val deserialized = parser.deserializeToDocument<Person>(documentJson)
        val serialized = parser.serializeItem(deserialized.data!!)

        Then {
            serialized equals documentJson
        }
    }

    @Test
    fun `deserialize and serialize back resource collection document`() {
        val documentJson = COLLECTION_OF_ARTICLES_ARTICLE_WITHOUT_INCLUDED_AND_META.asResource()
        val parser = `izzy with classes`(Article::class.java, Person::class.java)
        val deserialized = parser.deserializeToCollection<Article>(documentJson)
        val serialized = parser.serializeItemCollection(deserialized.data!!)

        Then {
            serialized equals documentJson
        }
    }

    @Test
    fun `not serialize relationships if all values are null`() {
        val article = Article()

        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .serializeItem(article)
        Then { result equals """{"data":{"type":"articles"}}""" }
    }

    @Test
    fun `not serialize attributes if there are no attributes to serialize`() {
        val person = Person()

        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .serializeItem(person)
        Then { result equals """{"data":{"type":"persons"}}""" }
    }

    @Test
    fun `serialize empty relationships collection`() {
        val article = Article().apply { coauthors = arrayListOf() }

        val result = `izzy with classes`(Article::class.java, Person::class.java)
            .serializeItem(article)
        Then { result equals """{"data":{"type":"articles","relationships":{"coauthors":{"data":[]}}}}""" }
    }

    @Test
    fun `serialize nullable relationships with null data`() {
        val weapon = Weapon()

        val result = `izzy with classes`(Weapon::class.java, Shield::class.java)
            .serializeItem(weapon)

        Then { result equals """{"data":{"type":"weapons","relationships":{"shield":{"data":null}}}}""" }
    }

    @Test
    fun `serialize nullable relationships with null data in collection`() {
        val collection = arrayListOf(Weapon(), Weapon().apply { id = "10" })

        val result = `izzy with classes`(Weapon::class.java, Shield::class.java)
            .serializeItemCollection(collection)

        Then { result equals """{"data":[{"type":"weapons","relationships":{"shield":{"data":null}}},{"id":"10","type":"weapons","relationships":{"shield":{"data":null}}}]}""" }
    }

    @Test
    fun `serialize nullable attributes as null values`() {
        val shield = Shield()
        val result = `izzy with classes`(Shield::class.java)
            .serializeItem(shield)
        Then { result equals """{"data":{"type":"shields","attributes":{"strength":null}}}""" }
    }

    private fun `expected meta`(): HashMap<String, Any?>? {
        return linkedMapOf(
            "meta_key" to "meta_value",
            "customObject" to linkedMapOf("value" to "Hello")
        )
    }

    private fun `expected errors`(): Errors {
        return Errors(
            arrayListOf(
                Error(id = "20", code = "123", title = "Error title"),
                Error(
                    detail = "Error details",
                    status = "400",
                    source = Source(pointer = "/data/attributes/key"),
                    meta = hashMapOf("food" to "hamburger", "drink" to "water")
                ),
                Error(
                    detail = "Some details",
                    source = Source(
                        parameter = "Some source parameter",
                        pointer = "/data/attributes/name"
                    )
                )
            )
        )
    }

    private fun `expected links`() = Links(
        self = Link("self-url"),
        last = Link("url-last"),
        related = Link("related-url", mapOf("meta_key" to "meta value"))
    )

    private fun `izzy with classes`(vararg classes: Class<out IzzyResource>) =
        Izzy(JacksonParser(IzzyConfiguration(arrayOf(*classes))))

    private fun `expected top level meta`() = mapOf(
        "hint" to "Hint"
    )

    private fun `expected resource meta`() = mapOf(
        "copyright" to "Copyright 2015 Example Corp.",
        "author" to linkedMapOf("name" to "John")
    )
}
