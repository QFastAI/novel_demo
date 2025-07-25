package com.aiso.qfast.utils

object ByteUtils {

    fun word2Byte(ba: ByteArray, i: Int, s: Short) {
        ba[i] = (s.toInt() shr 8).toByte()
        ba[i + 1] = s.toByte()
    }

    fun bytesToString(ba: ByteArray?): String? {
        if (ba != null && ba.size > 0) {
            val sb = StringBuffer(ba.size * 2)

            for (i in ba.indices) {
                if ((ba[i].toInt() and 255) < 16) {
                    sb.append("0")
                }

                sb.append((ba[i].toInt() and 255).toLong().toString(16))
            }
            return sb.toString()
        } else {
            return null
        }
    }

    fun subByte(ba: ByteArray, start: Int, end: Int): ByteArray? {
        var e = end
        val size = ba.size
        if (start >= 0 && start + e <= size) {
            if (e < 0) {
                e = ba.size - start
            }

            val baNew = ByteArray(e)

            for (i in 0 until e) {
                baNew[i] = ba[i + start]
            }

            return baNew
        } else {
            return null
        }
    }
}
