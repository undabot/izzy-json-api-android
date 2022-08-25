package com.undabot.izzy.models

import com.undabot.izzy.parser.ID
import com.undabot.izzy.parser.JsonElements
import com.undabot.izzy.parser.SidePosting
import java.util.*

open class IzzyResource(
    var id: String? = null,
    var links: Links? = null,
    var meta: Map<String, Any?>? = null,
    val method: String? = null,
) {
    val tempId: String?

    init {
        this.tempId = if (method == SidePosting.METHOD_CREATE) UUID.randomUUID().toString() else null
    }

    companion object {
        fun from(relationshipData: JsonElements) =
            IzzyResource(relationshipData.stringFor(ID)!!)
    }
}
