package com.undabot.izzy

import com.undabot.izzy.annotations.Nullable
import com.undabot.izzy.models.IzzyResource
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType
import java.lang.reflect.Array as ArrayType

fun Type.isCollection() = Collection::class.java.isAssignableFrom(this.rawType)

fun Class<*>.annotatedWith(filterBy: Class<out Annotation>) =
    declaredFields.filter { it.isAnnotationPresent(filterBy) }

fun IzzyResource.typeFromResource() = this.javaClass.getAnnotation(com.undabot.izzy.annotations.Type::class.java).type

fun <T : IzzyResource> T.nonNullFields() =
    this.javaClass.declaredFields
        .map { it.apply { isAccessible = true } }
        .filter { it.isAnnotationPresent(Nullable::class.java) || it.get(this) != null }
        .map { Pair(it, it.get(this)) }

val Type.rawType: Class<*>
    get() = when (this) {
        is Class<*> -> this
        is ParameterizedType -> this.rawType as Class<*>
        is GenericArrayType -> ArrayType.newInstance(genericComponentType.rawType, 0).javaClass
        is TypeVariable<*> -> Object::class.java
        is WildcardType -> this.upperBounds[0].rawType
        else -> throw IllegalArgumentException("Expected a Class, ParameterizedType, or " +
            "GenericArrayType, but <$this> is of type ${this.javaClass.name}")
    }