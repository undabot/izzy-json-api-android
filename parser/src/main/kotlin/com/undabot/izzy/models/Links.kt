package com.undabot.izzy.models

data class Links(
    var self: Link? = null,
    var first: Link? = null,
    var last: Link? = null,
    var prev: Link? = null,
    var next: Link? = null,
    var related: Link? = null
)
