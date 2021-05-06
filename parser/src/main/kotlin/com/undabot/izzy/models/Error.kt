package com.undabot.izzy.models

data class Error(
    var id: String? = null,
    var status: String? = null,
    var code: String? = null,
    var title: String? = null,
    var detail: String? = null,
    var source: Source? = null,
    var meta: Map<String, Any?>? = null,
    var customProperties: Map<String, Any?>? = null
)