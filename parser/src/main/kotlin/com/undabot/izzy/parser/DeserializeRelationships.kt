package com.undabot.izzy.parser

import com.undabot.izzy.annotatedWith
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.exceptions.TypeNotRegisteredException
import com.undabot.izzy.models.ClassInstance
import com.undabot.izzy.models.DataPool
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.RelationshipFields
import com.undabot.izzy.models.Resource
import com.undabot.izzy.models.ResourceID
import com.undabot.izzy.typeFromResource
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

class DeserializeRelationships(
    private val izzyJsonParser: IzzyJsonParser,
    private val deserializeLinks: DeserializeLinks = DeserializeLinks(),
    private val deserializeMeta: DeserializeMeta = DeserializeMeta()
) {

    fun <T : IzzyResource> forGiven(dataPool: DataPool, resource: T?, data: JsonElements, jsonTree: JsonElements) {
        if (resource != null && data.has(RELATIONSHIPS)) {
            val currentPool = dataPool.apply {
                putResourceRelationshipsToPool(resource, data.jsonElement(RELATIONSHIPS), this)

                if (jsonTree.has(INCLUDED)) {
                    putIncludedResourcesIntoThePool(jsonTree.jsonElementsArray(INCLUDED), this)
                }
            }
            resourceWithRelationships(resource,
                    data.jsonElement(RELATIONSHIPS),
                    resource.typeFromResource(), dataPool)
            RelationshipMatcher().match(currentPool)
        }
    }

    private fun <T : IzzyResource> putResourceRelationshipsToPool(
        resource: T,
        relationshipObject: JsonElements,
        pool: DataPool
    ) {
        RelationshipFields().apply {
            resource::class.java.annotatedWith(Relationship::class.java)
                    .forEach { relationshipField ->
                        val relationshipName = relationshipField.getAnnotation(Relationship::class.java).name

                        if (relationshipObject.hasNonNull(relationshipName)) {
                            val relationshipData = getRelationshipDataFrom(relationshipObject, relationshipName)
                            if (relationshipData.isArray()) {
                                relationshipData.asArray().forEach { addRelationshipWithoutDataToPool(it, pool) }
                            } else if (relationshipData.isObject()) {
                                addRelationshipWithoutDataToPool(relationshipData, pool)
                            }
                        }
                    }
        }
    }

    private fun putIncludedResourcesIntoThePool(jsonData: List<JsonElements>, pool: DataPool) = pool
            .apply {
                jsonData.filter { hasRegisteredTypeFor(it.stringFor(TYPE)!!) }
                        .forEach { resourceJson ->
                            val id = resourceJson.stringFor(ID)!!
                            val type = resourceJson.stringFor(TYPE)!!
                            val realType = typeFor(type)
                            val classInstance = ClassInstance(realType,
                                    izzyJsonParser.parse(resourceJson.jsonElement(ATTRIBUTES).asJsonString(),
                                            realType))
                            val resource = classInstance.instance.apply {
                                this.id = id
                                this.links = deserializeLinks.from(resourceJson)
                                this.meta = deserializeMeta.from(resourceJson)
                            }
                            if (resourceJson.has(RELATIONSHIPS))
                                resourceWithRelationships(
                                        resource,
                                        resourceJson.jsonElement(RELATIONSHIPS),
                                        type, pool)
                            else
                                resourceWithoutRelationships(ResourceID(id, type), classInstance, pool)
                        }
            }

    /**
     *  Adds resource and it's metadata and relationship data to the DataPool.
     *
     * @param resource a resource object which we'll be adding to pool
     * @param relationshipsJsonObject part of the JSON ("relationships" object) that has all of the relationship data for this resource
     * @param type type for this resource
     *
     */
    private fun <T : IzzyResource> resourceWithRelationships(
        resource: T,
        relationshipsJsonObject: JsonElements,
        type: String,
        pool: DataPool
    ) {
        val id = ResourceID(resource.id!!, type)
        val relationshipFields = RelationshipFields().apply {
            extractResourceRelationships(resource::class, relationshipsJsonObject, resource, pool)

            val relationships = resource::class.allSuperclasses
            relationships.forEach { kClass ->
                extractResourceRelationships(kClass, relationshipsJsonObject, resource, pool)
            }
        }

        pool.put(id, Resource(ClassInstance(resource::class.java, resource), relationshipFields))
    }

    private fun <T : IzzyResource> RelationshipFields.extractResourceRelationships(
        kClass: KClass<*>,
        relationshipsJsonObject: JsonElements,
        resource: T,
        pool: DataPool
    ) {
        kClass.java.annotatedWith(Relationship::class.java)
            .forEach { relationshipField ->
                val relationshipName =
                    relationshipField.getAnnotation(Relationship::class.java).name

                if (relationshipsJsonObject.has(relationshipName)) {
                    val relationshipData =
                        getRelationshipDataFrom(relationshipsJsonObject, relationshipName)
                    relationshipField.isAccessible = true
                    addToPool(relationshipData, relationshipField)
                    putResourceRelationshipsToPool(resource, relationshipsJsonObject, pool)
                }
            }
    }

    private fun resourceWithoutRelationships(resId: ResourceID, classInstance: ClassInstance, pool: DataPool) {
        pool.put(resId, Resource(classInstance, RelationshipFields()))
    }

    private fun addRelationshipWithoutDataToPool(it: JsonElements, pool: DataPool) {
        val id = it.stringFor(ID)!!
        val typeString = it.stringFor(TYPE)!!

        if (!hasRegisteredTypeFor(typeString)) {
            return
        }

        val realType = typeFor(typeString)
        val resourceId = ResourceID(id, typeString)
        if (pool.resourceForId(resourceId) == null) {
            pool.put(ResourceID(id, typeString),
                    Resource(ClassInstance(realType, realType.newInstance().apply { this.id = id }),
                            RelationshipFields()))
        }
    }

    private fun hasRegisteredTypeFor(typeString: String) =
            izzyJsonParser.izzyConfiguration().isRegistered(typeString)

    private fun getRelationshipDataFrom(relationshipsObject: JsonElements, name: String) =
            relationshipsObject.jsonElement(name).jsonElement(DATA)

    private fun typeFor(resType: String) = try {
        izzyJsonParser.izzyConfiguration().typeFor(resType)
    } catch (e: Exception) {
        throw TypeNotRegisteredException(resType)
    }
}