package com.undabot.izzy.parser

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GsonElements(private val json: com.google.gson.JsonElement?) : JsonElements {

    override fun asMap(): Map<String, *>? {
        return Gson().fromJson(json, object : TypeToken<Map<String, *>>() {}.type)
    }

    override fun isObject(): Boolean {
        return json?.isJsonObject ?: false
    }

    override fun stringFor(key: String): String? {
        izzyLogger.lastElementSearchedIn(key, json.toString())
        return json?.asJsonObject!![key]?.asString
    }

    override fun hasNonNull(key: String): Boolean {
        return json?.asJsonObject!![key]?.isJsonNull?.not() ?: false
    }

    override fun isNull(): Boolean {
        return json?.isJsonNull ?: true
    }

    override fun asString(): String? {
        return json?.asString
    }

    override fun asArray() = json?.asJsonArray?.map { GsonElements(it) }!!

    override fun isArray(): Boolean {
        return json?.isJsonArray ?: false
    }

    override fun asJsonString(): String {
        return json?.asJsonObject.toString()
    }

    override fun jsonElement(keyToFind: String): JsonElements {
        izzyLogger.lastElementSearchedIn(keyToFind, json.toString())
        return GsonElements(json?.asJsonObject?.get(keyToFind))
    }

    override fun has(keyToFind: String): Boolean {
        return json?.asJsonObject?.has(keyToFind) ?: false
    }

    override fun jsonElementsArray(keyToFind: String): List<JsonElements> {
        izzyLogger.lastElementSearchedIn(keyToFind, json.toString())
        return json?.asJsonObject?.getAsJsonArray(keyToFind)!!.map { GsonElements(it) }
    }
}
