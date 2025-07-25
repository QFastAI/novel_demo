package com.aiso.qfast.utils

import com.aiso.qfast.utils.json.JSONArrayAdapter
import com.aiso.qfast.utils.json.JSONObjectAdapter
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

object GsonProvider {
    val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(JSONObject::class.java, JSONObjectAdapter.sInstance)
            .registerTypeAdapter(JSONArray::class.java, JSONArrayAdapter.sInstance).create()
    }
}