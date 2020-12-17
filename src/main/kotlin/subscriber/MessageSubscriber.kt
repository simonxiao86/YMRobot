package com.yumi.kotlin.subscriber

import com.yumi.kotlin.actions.AtBotAction
import com.yumi.kotlin.actions.CommandAction
import com.yumi.kotlin.actions.MonitorAction
import com.yumi.kotlin.util.startsWith
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder

object MessageSubscriber {

    fun subscribe(builder: GroupMessageSubscribersBuilder) {
        builder.run {
            atBot {
                if (this.startsWith("/")) return@atBot
                AtBotAction.invoke(this)
            }

            atBot().not().apply {

                startsWith("/").invoke {
                    CommandAction.invoke(this)
                }

                invoke {  // 默认进入监听模式
                    MonitorAction.invoke(this)
                }
            }

        }
    }

}