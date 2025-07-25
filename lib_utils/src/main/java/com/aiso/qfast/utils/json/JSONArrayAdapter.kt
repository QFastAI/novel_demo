package com.aiso.qfast.utils.json

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.json.JSONArray
import org.json.JSONException
import java.lang.reflect.Type


class JSONArrayAdapter : JsonSerializer<JSONArray?>, JsonDeserializer<JSONArray?> {
    override fun serialize(
        src: JSONArray?,
        typeOfSrc: Type?,
        context: JsonSerializationContext
    ): JsonElement? {
        if (src == null) {
            return null
        }
        val jsonArray = JsonArray()
        for (i in 0 until src.length()) {
            val `object` = src.opt(i)
            val jsonElement = context.serialize(`object`, `object`.javaClass)
            jsonArray.add(jsonElement)
        }
        return jsonArray
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): JSONArray? {
        return if (json == null) {
            null
        } else try {
            JSONArray(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            throw JsonParseException(e)
        }
    }

    companion object {
        val sInstance = JSONArrayAdapter()
    }
}