package com.aiso.qfast.utils

import com.alibaba.fastjson.JSON
import java.lang.reflect.Type

inline fun <reified T> String?.toObj(): T? {
    return try {
        GsonProvider.gson.fromJson(this, T::class.java)
    } catch (e: Exception) {
        LogUtils.e("toObj error\n${e.message}")
        null
    }
}

inline fun <reified T> String?.toObj(type:Type): T? {
    return try {
        GsonProvider.gson.fromJson(this, type) as T
    } catch (e: Exception) {
        LogUtils.e("toObj error\n${e.message}")
        null
    }
}


inline fun <reified T> T.toJson(): String? {
    return try {
        GsonProvider.gson.toJson(this)
    } catch (e: Exception) {
        LogUtils.e("toObj error\n${e.message}")
        null
    }
}


fun <T> List<T>.toJsonString(): String {
    return JSON.toJSONString(this)
}

fun <T> String.toList(clazz: Class<T>): List<T> {
    return JSON.parseArray(this, clazz)
}
