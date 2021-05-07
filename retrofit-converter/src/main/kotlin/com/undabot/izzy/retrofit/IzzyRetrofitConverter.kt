package com.undabot.izzy.retrofit

import com.undabot.izzy.isCollection
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument
import com.undabot.izzy.parser.Izzy
import com.undabot.izzy.rawType
import com.undabot.izzy.retrofit.IzzyRetrofitConverter.JsonType.COLLECTION
import com.undabot.izzy.retrofit.IzzyRetrofitConverter.JsonType.SINGLE
import com.undabot.izzy.retrofit.IzzyRetrofitConverter.JsonType.UNKNOWN
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class IzzyRetrofitConverter(val izzy: Izzy) : Converter.Factory() {

    private val jsonDocumentType = JsonDocument<Any>()::class.java.rawType

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? =
        when (getJsonType(type)) {
            COLLECTION -> IzzyCollectionResponseBodyConverter<IzzyResource>(izzy)
            SINGLE -> IzzyResponseBodyConverter<IzzyResource>(izzy)
            UNKNOWN -> IzzyInvalidJsonResponseBodyConverter()
        }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>?,
        methodAnnotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): Converter<*, RequestBody>? =
        when (isResourceCollection(type)) {
            true -> IzzyCollectionRequestBodyConverter<IzzyResource>(izzy)
            false -> IzzyRequestBodyConverter<IzzyResource>(izzy)
        }

    // We don't handle string types so here we return null to let retrofit know and pass it to another converter
    override fun stringConverter(
        type: Type?,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): Converter<*, String>? = null

    private fun getJsonType(type: Type): JsonType =
        if (isJsonDocument(type)) {
            val isJsonCollection = (type as ParameterizedType).actualTypeArguments[0].isCollection()
            if (isJsonCollection) COLLECTION else SINGLE
        } else {
            UNKNOWN
        }

    private fun isResourceCollection(type: Type) = type.isCollection()

    private fun isJsonDocument(type: Type): Boolean = type.rawType == jsonDocumentType

    enum class JsonType {
        COLLECTION, SINGLE, UNKNOWN
    }
}
