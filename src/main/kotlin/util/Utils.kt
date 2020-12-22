package com.yumi.kotlin.util

import java.io.*
import java.text.SimpleDateFormat
import java.util.*


fun formatTime(millSec: Int): String? {
    return SimpleDateFormat("HH:mm:ss").format(Date(millSec * 1000L))
}

fun formatTimeToDay(millSec: Int): String? {
    return SimpleDateFormat("yyyy-MM-dd").format(Date(millSec * 1000L))
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

fun isAm(): Boolean {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return hour in 6..10
}

fun writeToFile(src: String, file: File) {
    if (!file.exists())
        file.createNewFile()
    FileWriter(file).apply {
        write(src)
        flush()
        close()
    }
}

suspend fun Int.simpleRate(hit: suspend () -> Unit) {
    if (Random().nextInt(100) < this)
        hit()
}

fun String.isEnglishOrDigit(): Boolean {
    for (c in this) {
        if (!c.isLetterOrDigit()) {
            println("$c is not LetterOrDigit")
            return false
        }

    }
    return true
}

fun String.dropLetters(): String {
    return this.filter {
        it.isDigit()
    }
}