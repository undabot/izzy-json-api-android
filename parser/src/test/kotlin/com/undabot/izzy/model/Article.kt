package com.undabot.izzy.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource

@JsonIgnoreProperties(ignoreUnknown = true)
@Type("articles")
data class Article(
    @JsonProperty("title") var title: String? = null,
    @JsonProperty("description") var description: String? = null,
    @JsonProperty("custom_object") var customObject: CustomObject? = null,
    @JsonProperty("keywords") var keywords: ArrayList<String>? = null
) : IzzyResource() {

    @Relationship("coauthors")
    var coauthors: List<Person>? = null
    @Relationship("author")
    var author: Person? = null
}