package com.undabot.izzy.retrofit

import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.parser.Izzy
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter

class IzzyCollectionRequestBodyConverter<T : IzzyResource>(
    private val izzy: Izzy
) : Converter<List<T>, RequestBody> {

    override fun convert(value: List<T>): RequestBody {
        val bodyString = izzy.serializeItemCollection(value)

        return RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON_API), bodyString)
    }
}