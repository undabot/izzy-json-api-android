package com.undabot.izzy.retrofit

import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument
import com.undabot.izzy.parser.Izzy
import okhttp3.ResponseBody
import retrofit2.Converter

class IzzyCollectionResponseBodyConverter<T : IzzyResource>(
    private val izzy: Izzy
) : Converter<ResponseBody, JsonDocument<List<T>>> {

    override fun convert(value: ResponseBody): JsonDocument<List<T>> =
            izzy.deserializeToCollection(value.charStream().readText())
}