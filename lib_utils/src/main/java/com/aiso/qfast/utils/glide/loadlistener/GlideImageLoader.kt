package com.aiso.qfast.utils.glide.loadlistener

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class GlideImageLoader {

    companion object {

        @OptIn(DelicateCoroutinesApi::class)
        fun load(url: String, listener: ProgressListener) {
            GlobalScope.launch(Dispatchers.IO) {
                val client = OkHttpClient.Builder()
                    .addNetworkInterceptor { chain ->
                        val originalResponse = chain.proceed(chain.request())
                        originalResponse.newBuilder()
                            .body(ProgressResponseBody(originalResponse.body(), listener)).build()
                    }.build()
                val request = Request.Builder().url(url).get().build()
                client.newCall(request).execute()
            }

        }
    }
}