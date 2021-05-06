package com.undabot.izzy.parser

import com.undabot.izzy.models.Error

class DeserializeError(
    private val deserializeSource: DeserializeSource = DeserializeSource(),
    private val deserializeMeta: DeserializeMeta = DeserializeMeta(),
    private val deserializeErrorCustomProperties: DeserializeErrorCustomProperties = DeserializeErrorCustomProperties()
) {

    private val idKey = "id"
    private val statusKey = "status"
    private val codeKey = "code"
    private val titleKey = "title"
    private val detailKey = "detail"
    private val sourceKey = "source"

    fun from(errorElement: JsonElements): Error {
        return Error(
            id = errorElement.stringFor(idKey),
            status = errorElement.stringFor(statusKey),
            code = errorElement.stringFor(codeKey),
            title = errorElement.stringFor(titleKey),
            detail = errorElement.stringFor(detailKey),
            source = deserializeSource.from(errorElement.jsonElement(sourceKey)),
            meta = deserializeMeta.from(errorElement),
            customProperties = deserializeErrorCustomProperties.from(
                errorElement,
                listOf(idKey, statusKey, codeKey, titleKey, detailKey, sourceKey, META)
            )
        )
    }
}