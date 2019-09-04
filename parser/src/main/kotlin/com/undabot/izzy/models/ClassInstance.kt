package com.undabot.izzy.models

/**
 * @author Ian Rumac
 * @param classData Real class for this resource
 * @param instance Instance of this resource casted as IzzyResource
 */
class ClassInstance(classData: Class<out IzzyResource>, instance: IzzyResource) {
    private val realValue = Pair(classData, instance)

    val classData = realValue.first

    val instance = realValue.second
}