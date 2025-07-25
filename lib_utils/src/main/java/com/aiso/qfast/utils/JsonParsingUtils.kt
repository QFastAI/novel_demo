package com.aiso.qfast.utils

import org.json.JSONObject

fun JSONObject.getStringOrNull(key: String): String? {
    return try {
        if (has(key)) {
            getString(key)
        } else {
            null
        }
    } catch (e: Exception) {
        println("Error getting string for key '$key': ${e.message}")
        null
    }
}

fun JSONObject.getIntOrNull(key: String): Int? {
    return try {
        if (has(key)) {
            getInt(key)
        } else {
           null
        }
    } catch (e: Exception) {
        println("Error getting int for key '$key': ${e.message}")
        null
    }
}

fun JSONObject.getLongArray(key: String): List<Long> {
    return try {
        val idList = mutableListOf<Long>()
        if (has(key)) {
            val jsonArray = getJSONArray(key)
            for (i in 0 until jsonArray.length()) {
                idList.add(jsonArray.getLong(i))
            }
            return idList
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        println("Error getting int for key '$key': ${e.message}")
        emptyList()
    }
}


fun JSONObject.getLongOrNull(key: String): Long? {
    return try {
        if (has(key)) {
            getLong(key)
        } else {
            null
        }
    } catch (e: Exception) {
        println("Error getting double for key '$key': ${e.message}")
        null
    }
}

fun JSONObject.getDoubleOrNull(key: String): Double? {
    return try {
        if (has(key)) {
            getDouble(key)
        } else {
            null
        }
    } catch (e: Exception) {
        println("Error getting double for key '$key': ${e.message}")
        null
    }
}