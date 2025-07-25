package com.aiso.qfast.utils.glide.loadlistener

interface ProgressListener {

    fun onProgress(bytesRead: Long, contentLength: Long, done: Boolean)
}