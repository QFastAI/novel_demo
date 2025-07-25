package com.aiso.qfast.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object MD5Utils {
    fun getMD5(str: String?): String? {
        return str?.let {
            try {
                val ba = it.toByteArray()
                val md = MessageDigest.getInstance("MD5")
                md.update(ba)
                ByteUtils.bytesToString(md.digest())
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getMD5(ba: ByteArray?): ByteArray? {
        return ba?.let {
            try {
                val md = MessageDigest.getInstance("MD5")
                md.update(it)
                md.digest()
            } catch (var2: Exception) {
                null
            }
        }
    }

    fun getMD5(file: File?): String? {
        return file?.let {
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(it)
                getMD5(fis)
            } catch (e: FileNotFoundException) {
                null
            } catch (e: IOException) {
                null
            } finally {
                try {
                    fis?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    }

    fun getMD5(stream: InputStream?): String? {
        return stream?.let {stream->
            try {
                var md: MessageDigest? = null
                try {
                    md = MessageDigest.getInstance("MD5")
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                    return null
                }
                val ba = ByteArray(8192)
                var i: Int
                while ((stream.read(ba).also { i = it }) != -1) {
                    md.update(ba, 0, i)
                }
                ByteUtils.bytesToString(md.digest())
            } catch (var5: Throwable) {
                null
            }
        }
    }
}
