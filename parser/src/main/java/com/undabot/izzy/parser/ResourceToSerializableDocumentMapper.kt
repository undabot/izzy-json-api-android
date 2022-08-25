package com.undabot.izzy.parser

import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.applyAction
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.nonNullFields
import com.undabot.izzy.parser.DataWrapper.Companion.NULLABLE_FIELD
import java.lang.reflect.Field
import kotlin.collections.ArrayList

class ResourceToSerializableDocumentMapper(private val relationshipFieldMapper: RelationshipFieldMapper) {

    /**
     * Maps resource to a {@link com.undabot.izzy.parser.SerializableDocument} containing relationships
     * @param resource resource to map
     */
    fun <T : IzzyResource> mapFrom(resource: T): SerializableDocument {
        val included = ArrayList<SerializableDocument>()

        return createSerializableDocument(resource, included, included)
    }

    private fun <T : IzzyResource> createSerializableDocument(
        resource: T,
        included: MutableList<SerializableDocument>,
        includedValue: List<SerializableDocument>?
    ): SerializableDocument {
        val fields = resource.nonNullFields()
            .groupBy { it.first.isAnnotationPresent(Relationship::class.java) }
        return SerializableDocument(
            resource.id,
            resource::class.java.getAnnotation(Type::class.java).type,
            getAttributesFor(fields, resource),
            createRelationshipsFor(fields[true] ?: emptyList(), included),
            includedValue,
            resource.tempId
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

    private fun createRelationshipsFor(
        list: List<Pair<Field, Any?>>,
        included: MutableList<SerializableDocument>
    ): Map<String, Any>? {

        list.forEach { pair ->
            pair.second?.applyAction {
                if (it !is IzzyResource) return@applyAction
                val sDocument = createSerializableDocument(it, included, null)
                included.add(sDocument)
            }
        }

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
    val relationships: Map<String, Any>?,
    val included: List<SerializableDocument>?,
    val `temp-id`: String? = null
) {
    fun toData() = SerializableDocument(
        id = this.id,
        type = this.type,
        attributes = this.attributes,
        relationships = this.relationships,
        included = null,
        `temp-id` = this.`temp-id`,
    )

    fun getIncludedList(): List<SerializableDocument>?{
        return if (included.isNullOrEmpty()) null else included
    }
}

object SidePosting {
    const val METHOD_UPDATE = "update"
    const val METHOD_CREATE = "create"
    const val METHOD_DISASSOCIATE = "disassociate"
    const val METHOD_DESTROY = "destroy"
}