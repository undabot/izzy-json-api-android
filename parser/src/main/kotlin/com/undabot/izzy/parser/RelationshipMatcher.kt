package com.undabot.izzy.parser

import com.undabot.izzy.isCollection
import com.undabot.izzy.models.DataPool
import com.undabot.izzy.models.Resource
import com.undabot.izzy.models.ResourceID
import java.lang.reflect.Field

class RelationshipMatcher {

    /**
     * Iterates over the
     * @param pool entries, matching each of entries relationships to table objects via ResourceID's and
     * setting them into the fields
     */
    fun match(pool: DataPool) {
        pool.entries
                .forEach { poolItem ->
                    if (relationshipsFound(poolItem)) {
                        val collectionRelationships = extractCollectionBasedRelationships(poolItem)
                        if (collectionRelationships.isNotEmpty()) {
                            createCollectionRelationships(collectionRelationships, poolItem, pool)
                        }
                        createOneToOneRelationships(poolItem, pool)
                    }
                }
    }

    /**
     * Takes an item and creates one-to-one relationships (so only non-collection objects)
     * for that item. Each resource that is defined for the relationship is found via their ResourceID
     * from the DataPool and set to the annotated field in the relationship owning object (the Resource in poolItem).
     * @param poolItem A resource from the pool for which we will be setting relationships
     * @param pool Used to find the relationship resources and tie them to the object.
     */
    private fun createOneToOneRelationships(poolItem: MutableMap.MutableEntry<ResourceID, Resource>, pool: DataPool) {
        poolItem.value
                .second
                .get()
                .filterNot { it.second.type.isCollection() }
                .forEach { resource ->
                    resource.second.set(poolItem.value.first.instance, pool.resourceForId(resource.first)?.first?.instance)
                }
    }

    /**
     * Takes an item and creates one-to-many relationships (so only collection objects)
     * for that item. Each resource that belongs to the collection is found via their ResourceID
     * from the DataPool and added to the list which is then set to the annotated field
     * in the relationship owning object (the Resource in poolItem).
     *
     * @param collectionRelationships A map of fields and the collection of ResourceID's the collection has
     * @param poolItem A resource from the pool for which we will be setting relationships
     * @param pool Used to find the relationship resources and tie them to the object.
     */

    private fun createCollectionRelationships(
        collectionRelationships: Map<ResourceID, Field>,
        poolItem: MutableMap.MutableEntry<ResourceID, Resource>,
        pool: DataPool
    ) {

        val relevantResources = HashMap<Field, MutableSet<ResourceID>>()
        collectionRelationships.forEach { action ->
            if (relevantResources[action.value] != null)
                relevantResources[action.value]!!.add(action.key)
            else relevantResources[action.value] = mutableSetOf(action.key)
        }
        relevantResources.forEach { field ->
            field.key.set(poolItem.value.first.instance,
                    field.value.map { id ->
                        pool.resourceForId(id)?.first?.instance
                                .apply {
                                    this!!.id = id.id
                                }
                    })
        }
    }

    private fun extractCollectionBasedRelationships(
        fromResource: MutableMap.MutableEntry<ResourceID, Resource>
    ): Map<ResourceID, Field> {
        return fromResource.value.second.get()
                .filter { entry -> entry.second.type.isCollection() }.toMap()
    }

    private fun relationshipsFound(poolItem: MutableMap.MutableEntry<ResourceID, Resource>) =
            poolItem.value.second.hasRelationships()
}
