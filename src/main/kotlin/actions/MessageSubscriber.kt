package com.yumi.kotlin.actions

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