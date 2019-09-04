package com.undabot.izzy.parser

import com.undabot.izzy.models.Error

class DeserializeError(
    private val deserializeSource: DeserializeSource = DeserializeSource(),
    private val deserializeMeta: DeserializeMeta = DeserializeMeta()
) {

    fun from(errorElement: JsonElements): Error {
        return Error(
                id = errorElement.stringFor("id"),
                status = errorElement.stringFor("status"),
                code = errorElement.stringFor("code"),
                title = errorElement.stringFor("title"),
                detail = errorElement.stringFor("detail"),
                source = deserializeSource.from(errorElement.jsonElement("source")),
                meta = deserializeMeta.from(errorElement)
        )
    }
}