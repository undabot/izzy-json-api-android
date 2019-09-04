package com.undabot.izzy.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.undabot.izzy.annotations.Nullable
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource

@Type("players")
data class Player(
    @JsonProperty("titles") var titles: List<String>? = null
) : IzzyResource() {

    @Relationship("items")
    var items: List<IzzyResource>? = null
}

@Type("weapons")
data class Weapon(
    @JsonProperty("hint") var hint: String? = null
) : IzzyResource() {

    @Nullable
    @Relationship("shield")
    var shield: Shield? = null
}

@Type("shields")
data class Shield(
    @JsonProperty("name") var name: String? = null,
    @Nullable @JsonProperty("strength") var strength: String? = null
) : IzzyResource()
