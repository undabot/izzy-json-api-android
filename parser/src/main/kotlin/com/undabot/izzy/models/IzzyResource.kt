package com.undabot.izzy.models

import com.undabot.izzy.parser.ID
import com.undabot.izzy.parser.JsonElements

open class IzzyResource(
    var id: String? = null,
    var links: Links? = null,
    var meta: Map<String, Any?>? = null
) {
    companion object {
        fun from(relationshipData: JsonElements) =
            IzzyResource(relationshipData.stringFor(ID)!!)
    }
}
