package com.yumi.kotlin.actions

import net.mamoe.mirai.message.GroupMessageEvent

interface MessageHandler {
    suspend fun invoke(event: GroupMessageEvent)
}