package com.yumi.kotlin

import java.text.SimpleDateFormat
import java.util.*

fun formatTime(millSec: Int): String? {
    return SimpleDateFormat("HH:mm:ss").format(Date(millSec * 1000L))
}

fun String?.isImage(): Boolean {
    return this != null
            && (this.endsWith("png")
            || this.endsWith("webp")
            || this.endsWith("jpg")
            || this.endsWith("jpeg"))
}