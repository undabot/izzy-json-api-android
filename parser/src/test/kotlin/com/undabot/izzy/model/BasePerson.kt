package com.undabot.izzy.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource

@JsonIgnoreProperties(ignoreUnknown = true)
@Type("base_persons")
open class BasePerson : IzzyResource() {

    @Relationship("supervisor")
    var supervisor: Person? = null
}
