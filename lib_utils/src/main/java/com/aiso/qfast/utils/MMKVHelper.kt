package com.aiso.qfast.utils

import android.content.Context
import android.os.Parcelable
import com.aiso.qfast.base.config.BuildConfig
import com.aiso.qfast.utils.LogUtils
import com.aiso.qfast.utils.toJson
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import org.json.JSONObject

/**
 *
 *  mmkv 工具类
 *
 *  lytodo: 切换用户时切换mmkv文件
 *
 * @author      Du
 * @date        2021/8/9 11:29 上午
 * @description mmkv管理器
 **/
object MMKVHelper {

    const val TAG = "MMKVHelper"

    private val globalKv: MMKVWrapper by lazy {
        MMKVWrapper("global_mmkv")
    }

    private var userKv: MMKVWrapper? = null

    fun init(context: Context) {
        MMKV.initialize(context)
    }

    fun setup(userId: Long) {
        if (userKv != null && userKv?.key != userId.toString()) {
            userKv?.close()
            userKv = null
        }
        userKv = MMKVWrapper(userId.toString())
    }

    fun getGlobalInstance() = globalKv

    fun getUserInstance(): MMKVWrapper {
        val userJson = getGlobalInstance().getString("mmkv_key_current_user").orEmpty()
        val userId = getGlobalInstance().getString("mmkv_key_current_user_id").orEmpty()
            .toLongOrNull()?.takeIf { it > 0 } ?: queryUserIdFromJson(userJson).takeIf { it > 0 }
        userId?.let { setup(it) }
        return userKv!!
    }


    private fun queryUserIdFromJson(json: String): Long {
        return try {
            val jsonObject = JSONObject(json)

            jsonObject.optJSONObject("userAccountResource")?.optLong("userId")?.takeIf { it > 0  }
                ?: jsonObject.optJSONObject("userCommonPropertyResource")?.optLong("userId")?.takeIf { it > 0 }
                ?: jsonObject.optJSONObject("userProfileResource")?.optLong("id")?.takeIf { it > 0 }
                ?: jsonObject.optJSONObject("userResource")?.optLong("id")?.takeIf { it > 0 } ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun clearAll() {
        globalKv.clear()
        userKv?.clear()
    }

    class MMKVWrapper(val key: String) {

        val kv: MMKV by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MMKV.mmkvWithID(key)
        }

        fun putCommon(name: String, value: Any?): Boolean {
            val result = when (value) {
                is Int -> {
                    kv.encode(name, value)
                }

                is Float -> {
                    kv.encode(name, value)
                }

                is Boolean -> {
                    kv.encode(name, value)
                }

                is String -> {
                    kv.encode(name, value)
                }

                is Long -> {
                    kv.encode(name, value)
                }

                is Double -> {
                    kv.encode(name, value)
                }

                else -> {
                    false
                }
            }
            return result
        }

        fun <T> putMap(name: String, value: Map<String, T>) {
            val gson = Gson()
            val jsonMap = gson.toJson(value)
            val result = kv.encode(name, jsonMap)
            LogUtils.d("Key=${name} ---> isSuccessSave=${result} ---> value=${jsonMap}")
        }

        inline fun <reified T> putList(name: String, value: List<T>) {
            val jsonList = value.toJson()
            val result = this.kv.encode(name, jsonList)
            if (BuildConfig.DEBUG){
                LogUtils.d("Key=${name} size:${value.size} ---> isSuccessSave=${result} ---> value=${jsonList}")
            }
        }

        inline fun <reified T> getMap(name: String, default: String = ""): MutableMap<String, T> {
            val gsonString = kv.decodeString(name, default)
            var map: MutableMap<String, T>? = null
            try {
                map = Gson().fromJson(gsonString, object : TypeToken<HashMap<String, T>>() {}.type)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            LogUtils.d("%s", "Key=${name} ---> formatJson=${gsonString} ---> getValue=${map}")
            return map ?: mutableMapOf()
        }

        inline fun <reified T> getList(name: String, default: String = ""): MutableList<T> {
            val gsonString = kv.decodeString(name, default)
            var resultList: MutableList<T>? = null
            try {
                resultList = GsonProvider.gson.fromJson(
                    gsonString,
                    object : TypeToken<MutableList<T>>() {}.type
                )
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (BuildConfig.DEBUG){
                LogUtils.d("%s", "Key=${name} ---> formatJson=${gsonString} ---> getValue=${resultList}")
            }
            return resultList ?: mutableListOf<T>()
        }

        inline fun <reified T : Any> get(name: String, default: T): T {
            if (!kv.containsKey(name)) {
                putCommon(name, default)
            }
            val value: Any?
            when (default) {
                is Int -> {
                    value = kv.decodeInt(name, default)
                }

                is Float -> {
                    value = kv.decodeFloat(name, default)
                }

                is Boolean -> {
                    value = kv.decodeBool(name, default)
                }

                is String -> {
                    value = kv.decodeString(name, default)
                }

                is Long -> {
                    value = kv.decodeLong(name, default)
                }

                is Double -> {
                    value = kv.decodeDouble(name, default)
                }

                else -> {
                    throw IllegalArgumentException("not found type")
                }
            }
            LogUtils.d("%s", "Key=${name} ---> getValue=${value}")

            return value as T
        }

        fun <T : Parcelable?> getParcelable(
            name: String,
            tClass: Class<T>?,
            default: T? = null,
        ): T? {
            return kv.decodeParcelable(name, tClass, default)
        }

        inline fun <reified T : Parcelable?> getParcelable(name: String, default: String = ""): T? {
            val gsonString = kv.decodeString(name, default)
            var tempAny: T? = null
            try {
                tempAny = Gson().fromJson(gsonString, object : TypeToken<T>() {}.type)
                LogUtils.d("%s", "pushData ---> into_push 点击取出 ${tempAny} gsonString=${gsonString}")
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                LogUtils.d("%s", "pushData ---> into_push 点击取出异常${e.message}")
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtils.d("%s", "pushData ---> into_push 点击取出异常${e.message}")
            }
            LogUtils.d("%s", "Key=${name} ---> formatJson=${gsonString} ---> getValue=${tempAny}")
            return tempAny
        }

        inline fun <reified T : Parcelable?> putParcelable(name: String, value: T) {
            try {
                val gson = Gson()
                val jsonList = gson.toJson(value)
                val result = kv.encode(name, jsonList)
                if (BuildConfig.DEBUG){
                    LogUtils.d("%s", "Key=${name} ---> isSuccessSave=${result} ---> value=${jsonList}")
                }
            } catch (e: java.lang.Exception) {
                LogUtils.d("%s", "pushData ---> into_push 点击存入${value}")
                e.printStackTrace()
            }
        }

        fun getInt(name: String, default: Int): Int = kv.decodeInt(name, default)
        fun getFloat(name: String, default: Float): Float = kv.decodeFloat(name, default)
        fun getBoolean(name: String, default: Boolean = false): Boolean =
            kv.decodeBool(name, default)

        fun getString(name: String): String? = kv.decodeString(name)
        fun getLong(name: String, default: Long): Long = kv.decodeLong(name, default)
        fun getDouble(name: String, default: Double): Double = kv.decodeDouble(name, default)

        fun remove(name: String) {
            kv.remove(name)
        }

        fun containsKey(key: String): Boolean {
            return kv.containsKey(key)
        }

        fun clear() {
            kv.clearAll()
        }

        fun clearMemoryCache() {
            kv.clearMemoryCache()
        }

        fun close() {
            kv.close()
        }
    }

}