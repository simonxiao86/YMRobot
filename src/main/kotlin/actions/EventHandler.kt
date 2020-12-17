package com.yumi.kotlin.actions

import net.mamoe.mirai.event.events.GroupEvent

interface EventHandler {
    suspend fun invoke(event: GroupEvent)
}