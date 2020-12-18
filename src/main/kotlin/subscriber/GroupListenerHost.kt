package com.yumi.kotlin.subscriber

import com.yumi.kotlin.actions.NewsAction
import com.yumi.kotlin.actions.RecallAction
import com.yumi.kotlin.isValidGroup
import com.yumi.kotlin.util.formatSecondsToString
import com.yumi.kotlin.util.isAm
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.GroupMessageEvent

object GroupListenerHost : SimpleListenerHost() {

    @EventHandler
    suspend fun GroupMessageEvent.onEvent() {
        RecallAction.addCache(this.message)
    }

    @EventHandler
    suspend fun GroupMuteAllEvent.onEvent() {
        val text = if (this.new) "(*^▽^*)关灯啦~~" else "(*^▽^*)开灯啦~~"
        if (this.group.isValidGroup()) {
            this.group.sendMessage(text)
            if (!this.new && isAm())
                NewsAction().invoke(this)
        }
    }

    @EventHandler
    suspend fun MemberMuteEvent.onEvent() {
        val text =
            "✿✿ヽ(°▽°)ノ✿恭喜\"${member.nameCardOrNick}\"喜提禁言套餐一份~~\n套餐剩余时间：${durationSeconds.formatSecondsToString()}"
        if (group.isValidGroup()) {
            group.sendMessage(text)
        }
    }

    @EventHandler
    suspend fun MemberUnmuteEvent.onEvent() {
        val text = "✿✿ヽ(°▽°)ノ✿恭喜\"${this.member.nameCardOrNick}\"走出小黑屋~~"
        if (this.group.isValidGroup()) {
            this.group.sendMessage(text)
        }
    }

    @EventHandler
    suspend fun MessageRecallEvent.GroupRecall.onEvent() {
        RecallAction.invoke(this)
    }

}