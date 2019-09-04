package com.undabot.izzy.parser

import com.google.gson.Gson

class GsonElementsShould : JsonElementsShould() {

    private val parser = GsonParser(IzzyConfiguration(), Gson())

    override fun parseJsonToJsonElements(json: String): JsonElements {
        return parser.parseToJsonElements(json)
    }
}