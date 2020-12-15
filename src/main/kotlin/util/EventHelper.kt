package com.yumi.kotlin.util

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText

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