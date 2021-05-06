package com.undabot.izzy.jackson

import com.undabot.izzy.parser.JsonElements
import com.undabot.izzy.parser.JsonElementsShould

class JacksonElementsShould : JsonElementsShould() {

    private val jacksonParser = JacksonParser()

    override fun `parseJsonToJsonElements`(json: String): JsonElements {
        return jacksonParser.parseToJsonElements(json)
    }
}
