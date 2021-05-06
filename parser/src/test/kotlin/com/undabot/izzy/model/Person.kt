package com.undabot.izzy.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type

@JsonIgnoreProperties(ignoreUnknown = true)
@Type("persons")
data class Person(
    @JsonProperty("name") var name: String? = null
) : BasePerson() {

    @Relationship("favoriteArticle")
    var favoriteArticle: Article? = null
}
