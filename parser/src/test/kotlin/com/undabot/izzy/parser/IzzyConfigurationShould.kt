package com.undabot.izzy.parser

import com.undabot.izzy.Given
import com.undabot.izzy.Then
import com.undabot.izzy.equals
import com.undabot.izzy.model.Article
import com.undabot.izzy.models.IzzyResource
import org.junit.Test

class IzzyConfigurationShould {

    private lateinit var configuration: IzzyConfiguration

    @Test
    fun `return that type is registered when class is registered as a resource`() {
        Given { `registered resource type`() }
        Then { configuration.isRegistered("articles") equals true }
    }

    @Test
    fun `return that type or class is not registered as a resource`() {
        Given { configuration = IzzyConfiguration(arrayOf()) }
        Then { configuration.isRegistered("non-resource") }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throw exception when registered resource does not contain proper annotation`() {
        Given { `registered class doesn't have proper annotation`() }
        Then { configuration.typeFor("non-resource") }
    }

    private fun `registered resource type`() {
        configuration = IzzyConfiguration(arrayOf(Article::class.java))
    }

    private fun `registered class doesn't have proper annotation`() {
        configuration = IzzyConfiguration(arrayOf(NonResource::class.java))
    }
}

class NonResource : IzzyResource()
