package com.aiso.qfast.utils.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type


class JSONObjectAdapter : JsonSerializer<JSONObject?>, JsonDeserializer<JSONObject?> {

    companion object {
        var sInstance = JSONObjectAdapter()
    }

    override fun serialize(
        src: JSONObject?,
        typeOfSrc: Type?,
        context: JsonSerializationContext
    ): JsonElement? {
        if (src == null) {
            return null
        }
        val jsonObject = JsonObject()
        val keys = src.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = src.opt(key)
            val jsonElement = context.serialize(value, value.javaClass)
            jsonObject.add(key, jsonElement)
        }
        return jsonObject
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): JSONObject? {
        return if (json == null) {
            null
        } else try {
            JSONObject(json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            throw JsonParseException(e)
        }
    }

}



