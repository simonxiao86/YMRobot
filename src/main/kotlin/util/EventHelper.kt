package com.yumi.kotlin.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.uploadAsImage
import java.io.File

fun GroupMessageEvent.isManager() = sender.isManager()

fun Member?.isManager() = this?.permission?.level == 1 || this?.permission?.level == 2

fun MessageEvent.containsAny(vararg sub: String, ignoreCase: Boolean = false): Boolean {
    for (s in sub) {
        if (this.message[PlainText]?.content?.contains(s, ignoreCase) == true)
            return true
    }
    return false
}

fun MessageEvent.startsWith(sub: String, ignoreCase: Boolean = false): Boolean {
    return this.message[PlainText]?.content?.startsWith(sub, ignoreCase) ?: false
}

fun MessageEvent.contains(sub: String, ignoreCase: Boolean = false): Boolean {
    return this.message[PlainText]?.content?.contains(sub, ignoreCase) ?: false
}

fun MessageEvent.outOfLength(maxLength: Int): Boolean {
    return this.message[PlainText]?.content?.length ?: 0 > maxLength
}

suspend fun GroupMessageEvent.sendImage(url: String) {
    val suffix = url.drop(url.lastIndexOf("."))
    val file = File.createTempFile("./tmp/${System.currentTimeMillis()}", suffix)
    try {
        val byteArray = HttpClient(OkHttp).get<ByteArray>(url)
        file.writeBytes(byteArray)
        var count = 0
        while (count < 10) {
            try {
                val image = file.uploadAsImage(group)
                reply(image)
                break
            } catch (e: Exception) {
                count++
            }
        }
    } finally {
        file.delete()
    }
}