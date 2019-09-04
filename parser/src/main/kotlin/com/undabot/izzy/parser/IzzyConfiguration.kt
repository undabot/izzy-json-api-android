package com.undabot.izzy.parser

import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource

class IzzyConfiguration(resourceTypes: Array<Class<out IzzyResource>> = arrayOf()) {

    private val registeredTypes: HashMap<String, Class<out IzzyResource>> = registeredTypesFrom(resourceTypes)

    fun isRegistered(type: String): Boolean = registeredTypes.containsKey(type)

    fun typeFor(resourceType: String): Class<out IzzyResource> = registeredTypes[resourceType]!!

    private fun registeredTypesFrom(resourceTypes: Array<Class<out IzzyResource>>): HashMap<String, Class<out IzzyResource>> {
        val map: HashMap<String, Class<out IzzyResource>> = hashMapOf()

        resourceTypes.iterator().forEach { resourceClass ->
            if (resourceClass.isAnnotationPresent(Type::class.java)) {
                map[resourceClass.getAnnotation(Type::class.java).type] = resourceClass
            } else {
                throw IllegalArgumentException("Provided $resourceClass is missing com.undabot.izzy.annotations.Type annotation!")
            }
        }

        return map
    }
}
