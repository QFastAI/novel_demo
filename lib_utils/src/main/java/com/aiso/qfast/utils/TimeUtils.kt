package com.aiso.qfast.utils

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {
    @SuppressLint("NewApi")
    fun formatUtcTime(utcTime: String,format:String = "yyyy-MM-dd HH:mm"): String {
        val zonedDateTime = ZonedDateTime.parse(utcTime) // 自动解析 ISO-8601 格式
        val outputFormatter = DateTimeFormatter.ofPattern(format)
        return outputFormatter.format(zonedDateTime)
    }

    @SuppressLint("NewApi")
    fun formatToIso8601(dateStr: String): String {
        // 先处理不规范的日期格式（如1996-4-28 → 1996-04-28）
        val parts = dateStr.split("-")
        val normalizedDate = "%04d-%02d-%02d".format(
            parts[0].toInt(),
            parts[1].toInt(),
            parts[2].toInt()
        )

        // 解析为标准LocalDate
        val date = LocalDate.parse(normalizedDate)

        // 添加固定时间部分（01:46:56.89）并转换为UTC
        return date.atTime(1, 46, 56, 890_000_00)
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT)
    }
}