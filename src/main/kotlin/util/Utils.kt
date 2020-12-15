package com.yumi.kotlin.util

import java.text.SimpleDateFormat
import java.util.*

fun formatTime(millSec: Int): String? {
    return SimpleDateFormat("HH:mm:ss").format(Date(millSec * 1000L))
}

fun Int.formatSecondsToString(): String {
    if (this < 60) {
        return "$this 秒"
    }
    var result = this / 60L
    if (result < 60) {
        return "$result 分钟"
    }
    var sub = result.toInt() % 60
    result /= 60L
    if (result < 24) {
        return if (sub == 0) "$result 小时 " else "$result 小时 $sub 分钟"
    }
    sub = result.toInt() % 24
    result /= 24L
    return if (sub == 0) "$result 天" else "$result 天 $sub 小时"
}

fun String?.isImage(): Boolean {
    return this != null
            && (this.endsWith("png")
            || this.endsWith("webp")
            || this.endsWith("jpg")
            || this.endsWith("jpeg"))
}
