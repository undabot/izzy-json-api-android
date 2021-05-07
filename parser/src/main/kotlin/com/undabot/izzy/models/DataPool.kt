package com.undabot.izzy.models

typealias Resource = Pair<ClassInstance, RelationshipFields>

/**
 * DataPool contains all of the resources, their instances and their relationship data
 */
class DataPool {

    private val dataTable = LinkedHashMap<ResourceID, Resource>()

    /**
     *  Puts the resource together with it's instance and relationship metadata
     *  into the DataPool for later usage.
     *  @param id resourceID is a id+type combination for specified resource under which we can
     *  retrieve the resource later
     *  @param resource a pair of the ClassInstance metadata and RelationshipFields data about
     *  relationships this resource has
     */
    fun put(id: ResourceID, resource: Resource) =
        dataTable.put(id, resource)

    fun resourceForId(id: ResourceID) = dataTable[id]

    val entries = dataTable.entries
}
