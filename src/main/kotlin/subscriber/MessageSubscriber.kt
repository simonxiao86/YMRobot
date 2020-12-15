package com.yumi.kotlin.subscriber

import com.yumi.kotlin.actions.AtBotAction
import com.yumi.kotlin.actions.CommandAction
import com.yumi.kotlin.actions.FansAction
import com.yumi.kotlin.actions.ManagerAction
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder

object MessageSubscriber {

    fun subscribe(builder: GroupMessageSubscribersBuilder) {
        builder.run {

            atBot {
                AtBotAction.invoke(this)
            }

            atBot().not().apply {

                print("common message\n")

                containsAny("好厉害", "大佬", "牛逼", "好棒").invoke {
                    FansAction.invoke(this)
                }

                startsWith("/").invoke {
                    CommandAction.invoke(this)
                }

                invoke {  // 默认进入管理模式
                    ManagerAction.invoke(this)
                }
            }

        }
    }

}