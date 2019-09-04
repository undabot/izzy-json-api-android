package com.undabot.izzy.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.undabot.izzy.models.IzzyResource

data class PointItems(
    @JsonProperty("items") var items: List<Points> = arrayListOf()
) : IzzyResource()

data class Points(
    @JsonProperty("first_name") var firstName: String = "",
    @JsonProperty("points") var points: String = ""
) : IzzyResource() {

    companion object {
        const val testJsonObject = "{\"first_name\": \"Ransom\",\n  \"points\": 78\n}"
        const val testJsonList = "{\"items\":[{\"first_name\": \"Ransom\",\n  \"points\": 78\n}," +
                " {\n\"first_name\": \"Mechelle\",\n\"points\": 25\n}]}"
        val firstTestItem = Points().apply {
            firstName = "Ransom"
            points = "78"
        }

        val secondTestItem = Points().apply {
            firstName = "Mechelle"
            points = "25"
        }
    }
}