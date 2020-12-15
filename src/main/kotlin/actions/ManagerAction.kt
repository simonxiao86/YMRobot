package com.yumi.kotlin.actions

import com.yumi.kotlin.util.contains
import com.yumi.kotlin.util.containsAny
import com.yumi.kotlin.util.yes
import net.mamoe.mirai.message.GroupMessageEvent

object ManagerAction : MessageHandler {
    override suspend fun invoke(event: GroupMessageEvent) {
        event.apply {
            containsAny("番号", "pornhub").yes {
                quoteReply("~~请不要开车哦，小蜜会逮捕你哒~~o(*^＠^*)o")
            }
            containsAny("sb", "傻逼", "fuck", "cnm", "bitch", "尼玛", "傻屌", "沙雕")
                .and(this.message.last().toString().contains("http").not())
                .yes {
                    quoteReply("(*╹▽╹*)~争做文明糖果人，小面提醒你要文明用语哦~~")
                }
            containsAny("几把", "叽霸", "jb")
                .and(this.message.last().toString().contains("http").not())
                .yes {
                    quoteReply("(*╹▽╹*)~ 说 J 不带 B，争做文明糖果人，小面提醒你要文明用语哦~~")
                }
            contains("发了一个“口令红包”").yes {
                quoteReply("(*╹▽╹*)~~亲亲，你涉嫌刷屏了哦，小蜜会逮捕你哒~~")
            }
        }
    }
}