package com.undabot.izzy.models

import com.undabot.izzy.annotations.Type
import com.undabot.izzy.parser.JsonElements
import java.lang.reflect.Field

class RelationshipFields {
    private val map = mutableListOf<Pair<ResourceID, Field>>()

    fun addToPool(relationshipData: JsonElements, relationshipField: Field) {
        if (relationshipData.isArray())
            addToPoolFromArray(relationshipData, relationshipField)
        else if (relationshipData.isObject())
            addToPoolFromObject(relationshipData, relationshipField)
    }

    /**
     * Used to add a relationship that is just a single object
     * @param relationshipData The JSON Node of this object.
     * @param relationshipField Field for this object belonging to owner of the relationship.
     * We'll later use this data to set that field in the instance object to value of the resolved relationship
     */
    private fun addToPoolFromObject(relationshipData: JsonElements, relationshipField: Field) {
        add(
            resourceIDFrom(relationshipData, relationshipField.type.getAnnotation(Type::class.java).type),
            relationshipField
        )
    }

    /**
     * Creates a ResourceID from this JSON object
     * @param relationshipData The JSON Node of this object.
     * @param type Type of this resource
     */
    private fun resourceIDFrom(relationshipData: JsonElements, type: String) =
        ResourceID(IzzyResource.from(relationshipData).id!!, type)

    /**
     * Used to add a relationship that is a collection
     * @param relationshipData The JSON Node of the collection.
     * @param relationshipField Field for this object belonging to owner of the relationship.
     * We'll later use this data to set that field in the instance object to value of the resolved relationship
     */

    private fun addToPoolFromArray(relationshipData: JsonElements, relationshipField: Field) {
        relationshipData.asArray()
            .map { Pair(IzzyResource.from(it), it.stringFor("type")) }
            .forEach { res ->
                add(ResourceID(res.first.id!!, res.second!!), relationshipField)
            }
    }

    fun add(resourceUnique: ResourceID, field: Field) = map.add(resourceUnique to field)

    fun hasRelationships() = map.isNotEmpty()

    fun get() = map

    fun fieldOrNull(resourceUnique: ResourceID) = map.find { it.first == resourceUnique }
}
