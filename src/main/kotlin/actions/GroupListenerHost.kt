package com.yumi.kotlin.actions

import MaxSizeHashMap
import com.yumi.kotlin.util.formatTime
import com.yumi.kotlin.isValidGroup
import com.yumi.kotlin.util.formatSecondsToString
import com.yumi.kotlin.util.isManager
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.id

object GroupListenerHost : SimpleListenerHost() {

    private val cache: MaxSizeHashMap<Int, MessageChain?> = MaxSizeHashMap(50)

    private var antiRecall: LinkedHashMap<Long, Boolean> =
        linkedMapOf(Pair(957968887L, true), Pair(656238669L, true), Pair(181398081L, true))

    fun setAntiRecallConfig(groupId: Long, isAnti: Boolean) {
        antiRecall[groupId] = isAnti
    }

    @EventHandler
    suspend fun GroupMessageEvent.onEvent() {
        cache[this.message.id] = this.message
    }

    @EventHandler
    suspend fun GroupMuteAllEvent.onEvent() {
        val text = if (this.new) "(*^▽^*)关灯啦~~" else "(*^▽^*)开灯啦~~"
        if (this.group.isValidGroup()) {
            this.group.sendMessage(text)
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
        if (antiRecall[this.group.id] != true) return
        if (this.group.isValidGroup()) {
            // 管理员的撤回操作不处理
            if (operator.isManager()) return
            val name = author.nameCardOrNick
            val time = formatTime(messageTime)
            val message = cache[messageId] ?: return
            this.group.sendMessage(PlainText("$name $time 撤回了一条消息：\n").plus(message))
        }
    }

}