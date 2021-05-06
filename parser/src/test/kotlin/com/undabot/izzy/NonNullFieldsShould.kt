package com.undabot.izzy

import com.undabot.izzy.model.Weapon
import org.junit.Test

class NonNullFieldsShould {

    @Test
    fun `include null fields annotated with @Nullable annotation`() {
        val weapon = Weapon()
        val expectedField = weapon.javaClass.declaredFields
            .map {
                it.isAccessible = true
                it
            }[0]

        val result = weapon.nonNullFields()

        result equals arrayListOf(expectedField to null)
    }
}
