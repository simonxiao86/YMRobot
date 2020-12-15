package com.yumi.kotlin.actions

import com.yumi.kotlin.util.containsAny
import net.mamoe.mirai.message.GroupMessageEvent
import java.io.File

object FansAction : MessageHandler {
    override suspend fun invoke(event: GroupMessageEvent) {
        event.run {
            val image = when {
                containsAny("低调", "龙哥") -> {
                    File("asset/didiao.jpg").uploadAsImage()
                }
                containsAny("群主", "200", "法老", "法法", "法海") -> {
                    File("asset/200.png").uploadAsImage()
                }
                containsAny("alex", "aa", "a少", ignoreCase = true) -> {
                    File("asset/Alex.jpg").uploadAsImage()
                }
                else -> {
                    null
                }
            }
            image?.let { reply(it) }
        }
    }
}