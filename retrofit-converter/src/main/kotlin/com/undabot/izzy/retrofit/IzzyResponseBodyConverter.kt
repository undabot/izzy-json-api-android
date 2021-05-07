package com.undabot.izzy.retrofit

import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument
import com.undabot.izzy.parser.Izzy
import okhttp3.ResponseBody
import retrofit2.Converter

class IzzyResponseBodyConverter<T : IzzyResource>(
    val izzy: Izzy
) : Converter<ResponseBody, JsonDocument<T>> {

    override fun convert(value: ResponseBody): JsonDocument<T> =
        izzy.deserializeToDocument(value.charStream().readText())
}
