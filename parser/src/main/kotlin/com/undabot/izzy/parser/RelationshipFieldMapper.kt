package com.undabot.izzy.parser

import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.isCollection
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.ResourceID
import java.lang.reflect.Field

class RelationshipFieldMapper {

    /**
     * Maps fields marked as relationships and their instances to a map of collection and non-collection items,
     * paired by their JSON-API relationship name and their ID-Type representation.
     * @param fieldRes a list of relationship annotated fields with instance as second Pair member.
     * */
    fun map(fieldRes: List<Pair<Field, Any?>>): Set<Pair<String, Any>> {
        val fields = fieldRes.groupBy { it.first.type.isCollection() }

        return fields.getOrElse(false) { emptyList() }
            .map { Pair(relationshipName(it.first), resourceIdFromResource(it)) }
            .union(fields.getOrElse(true) { emptyList() }.map { mapToRelationshipCollection(it) })
    }

    private fun resourceIdFromResource(fieldInstance: Pair<Field, Any?>): DataWrapper {
        return if (fieldInstance.second != null) {
            DataWrapper(ResourceID((fieldInstance.second as IzzyResource).id!!, type(fieldInstance.first.type)))
        } else {
            DataWrapper(DataWrapper.NULLABLE_FIELD)
        }
    }

    private fun mapToRelationshipCollection(relationshipField: Pair<Field, Any?>): Pair<String, Any> {
        val list = (relationshipField.second as Iterable<out IzzyResource>)
        val name = relationshipName(relationshipField.first)
        return if (list.none()) {
            name to DataWrapper(emptyList<Any>())
        } else {
            val type = type(list.first().javaClass)
            name to DataWrapper(list.map { ResourceID(it.id!!, type) })
        }
    }

    private fun relationshipName(relationshipField: Field) =
        relationshipField.getAnnotation(Relationship::class.java).name

    private fun type(classInstance: Class<*>) =
        classInstance.getAnnotation(Type::class.java).type
}

data class DataWrapper(val data: Any?) {
    companion object {
        const val NULLABLE_FIELD = Integer.MAX_VALUE.toString() + "nullable_resource_json_api_placeholder"
    }
}
