package com.yumi.kotlin.actions

import com.yumi.kotlin.subscriber.GroupListenerHost
import com.yumi.kotlin.util.contains
import com.yumi.kotlin.util.isManager
import com.yumi.kotlin.util.startsWith
import com.yumi.kotlin.util.yes
import net.mamoe.mirai.message.GroupMessageEvent


object CommandAction : MessageHandler {

    override suspend fun invoke(event: GroupMessageEvent) {
        event.apply {
            startsWith("/recall").and(contains("on")).and(isManager()).yes {
                GroupListenerHost.setAntiRecallConfig(this.group.id, true)
            }
            startsWith("/recall").and(contains("off")).and(isManager()).yes {
                GroupListenerHost.setAntiRecallConfig(this.group.id, false)
            }
            startsWith("/女装大佬").yes {
                BoysAction.invoke(this)
            }
        }
    }
}