package com.undabot.izzy

import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.models.ClassInstance
import com.undabot.izzy.models.DataPool
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.RelationshipFields
import com.undabot.izzy.models.Resource
import com.undabot.izzy.models.ResourceID
import com.undabot.izzy.parser.DATA
import com.undabot.izzy.parser.JsonElements

class AddResourceToPool(private val pool: DataPool) {

    /**
     *  Adds resource and it's metadata and relationship data to the DataPool.
     *
     * @param resource a resource object which we'll be adding to pool
     * @param relationshipsJsonObject part of the JSON ("relationships" object) that has
     * all of the relationship data for this resource
     * @param type type for this resource
     *
     */
    fun <T : IzzyResource> resourceWithRelationships(resource: T, relationshipsJsonObject: JsonElements, type: String) {
        val id = ResourceID(resource.id!!, type, resource.method, resource.tempId)
        val relationshipFields = RelationshipFields().apply {
            resource::class.java.annotatedWith(Relationship::class.java)
                .forEach { relationshipField ->
                    val relationshipName = relationshipField.getAnnotation(Relationship::class.java).name

                    if (relationshipsJsonObject.has(relationshipName)) {
                        val relationshipData = getRelationshipDataFrom(relationshipsJsonObject, relationshipName)
                        relationshipField.isAccessible = true
                        addToPool(relationshipData, relationshipField)
                    }
                }
        }

        pool.put(id, Pair(ClassInstance(resource::class.java, resource), relationshipFields))
    }

    fun resourceWithoutRelationships(resId: ResourceID, classInstance: ClassInstance) {
        pool.put(resId, Resource(classInstance, RelationshipFields()))
    }

    /*
    * Extracts relationship data from the relationship with the given name.
    * */

    private fun getRelationshipDataFrom(relationshipsObject: JsonElements, name: String) =
        relationshipsObject.jsonElement(name).jsonElement(DATA)
}
