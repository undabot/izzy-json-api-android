package com.undabot.izzy.parser

import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.nonNullFields
import com.undabot.izzy.parser.DataWrapper.Companion.NULLABLE_FIELD
import java.lang.reflect.Field

class ResourceToSerializableDocumentMapper(private val relationshipFieldMapper: RelationshipFieldMapper) {

    /**
     * Maps resource to a {@link com.undabot.izzy.parser.SerializableDocument} containing relationships
     * @param resource resource to map
     */
    fun <T : IzzyResource> mapFrom(resource: T): SerializableDocument {
        val fields = resource.nonNullFields()
            .groupBy { it.first.isAnnotationPresent(Relationship::class.java) }

        return SerializableDocument(
            resource.id,
            resource::class.java.getAnnotation(Type::class.java).type,
            getAttributesFor(fields, resource),
            createRelationshipsFor(fields[true] ?: emptyList())
        )
    }

    private fun <T : IzzyResource> getAttributesFor(
        fields: Map<Boolean, List<Pair<Field, Any?>>>,
        resource: T
    ): Map<String, Any?>? {
        val attributes: Map<String, Any?> = (fields[false] ?: emptyList<Pair<Field, Any>>())
            .associate { it.first.name to attributeValueFrom(it, resource) }
        return when (attributes.isEmpty()) {
            true -> null
            false -> attributes
        }
    }

    private fun <T : IzzyResource> attributeValueFrom(it: Pair<Field, Any?>, resource: T) =
        it.first.get(resource) ?: NULLABLE_FIELD

    private fun createRelationshipsFor(list: List<Pair<Field, Any?>>): Map<String, Any>? {
        val relationships = relationshipFieldMapper.map(list).toMap()
        return when (relationships.isEmpty()) {
            true -> null
            false -> relationships
        }
    }
}

data class SerializableDocument(
    val id: String?,
    val type: String,
    val attributes: Map<String, Any?>?,
    val relationships: Map<String, Any>?
)
