package com.aiso.qfast.utils.json

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type


/**
 * Create by lzan13 on 2020/7/30 17:19
 * 描述：Json 工具类
 */
object JsonUtils {

    val gson: Gson = GsonBuilder()
        .registerTypeAdapterFactory(GsonAdapterFactory())
        .create()

    /**
     * 将 json 转为对象
     */
    fun <T> fromJson(str: String, clazz: Type): T? {
        if (TextUtils.isEmpty(str)) {
            return null
        }
        return gson.fromJson(str, clazz)
    }

    /**
     * 将 json 转为集合
     */
    inline fun <reified T> fromJson(json: String): T{
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }

    /**
     * 将对象转为 json
     */
    fun <T> toJson(t: T?): String {
        if (t == null) {
            return ""
        }
        return gson.toJson(t)
    }

    /**
     * 将 map 集合转为 json 字符串
     */
    fun map2json(map: Map<String, Any>): String {
        return gson.toJson(map)
    }

    /**
     * 将 JSONObject 转换为 Map<String></String>, String> 对象
     * 只支持简单类型的转换为字符串
     * @param jsonObject 要转换的 JSONObject 对象
     * @return 转换后的 Map<String></String>, String> 对象
     * @throws JSONException 如果解析过程中出现 JSON 异常
     */
    @Throws(JSONException::class)
    fun jsonObjectToMap(jsonObject: JSONObject): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject[key]
            // 只处理简单类型的值，将其转换为字符串放入 Map 中
            map[key] = value.toString()
        }
        return map
    }
}