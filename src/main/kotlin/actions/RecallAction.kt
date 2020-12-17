package com.yumi.kotlin.actions

import MaxSizeHashMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yumi.kotlin.allValidGroups
import com.yumi.kotlin.data.RecallMember
import com.yumi.kotlin.isValidGroup
import com.yumi.kotlin.util.*
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.events.author
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.id
import java.io.File
import java.io.FileReader

object RecallAction : EventHandler {

    private const val MAX_RECALL_COUNT = 3

    private val cache: MaxSizeHashMap<Int, MessageChain?> = MaxSizeHashMap(50)

    private var members: MutableList<RecallMember>? = null

    private var antiRecall: MutableMap<Long, Boolean> = allValidGroups.associateWith { true }.toMutableMap()

    fun setAntiRecallConfig(groupId: Long, isAnti: Boolean): String {
        antiRecall[groupId] = isAnti
        if (isAnti) {
            loadMembers()
            return "已开启防撤回功能"
        }
        return "已关闭防撤回功能"
    }

    fun resetMembersFile() {
        val s = FileReader("./data/recall_members_default.json").readText()
        writeToFile(s, File("./data/recall_members.json"))
        loadMembers()
    }

    fun showMembers(groupId: Long, sender: Member): String? {
        if (antiRecall[groupId] == false)
            return "当前群未开启防撤回功能哦~请输入'/recall on'试试吧~"
        if (members == null) loadMembers()
        val validMembers = members?.filter {
            it.groupId == groupId &&
                    (it.recallCount > MAX_RECALL_COUNT || it.recallCount == -1)
        }
        if (sender.isManager()) {
            return if (validMembers.isNullOrEmpty()) {
                "当前防撤回名单中还没有人哦"
            } else {
                val s = validMembers.joinToString("\n") {
                    "${it.nickName}，今日已累计撤回：${it.recallCount}次"
                }
                "当前防撤回名单包含以下人员：\n$s"
            }
        } else {
            val find = validMembers?.find {
                it.qqId == sender.id
            }
            return if (find == null)
                "你不在防撤回名单中哦~"
            else
                "${sender.nameCardOrNick}，今日已累计撤回：${find.recallCount}次"
        }
    }

    fun addMember(member: Member, originCount: Int = 0): String {
        if (members == null) loadMembers()
        val index = findMember(member)
        if (index == null) {  // 没有则添加
            members?.add(RecallMember(member.group.id, member.id, member.nameCardOrNick, originCount))
            save()
            return "已添加${member.nameCardOrNick}至防撤回名单"
        }
        return "${member.nameCardOrNick}已经在防撤回名单里了哟~"
    }

    fun removeMember(member: Member): String {
        if (members == null) loadMembers()
        val index = findMember(member)
        if (index != null) {  // 有则删除
            members?.removeAt(index)
            save()
            return "已从防撤回名单中移除${member.nameCardOrNick}"
        }
        return "${member.nameCardOrNick}不在防撤回名单里哟~"
    }

    fun addCache(message: MessageChain) {
        cache[message.id] = message
    }

    private fun save() {
        writeToFile(Gson().toJson(members), File("./data/recall_members.json"))
    }

    private fun loadMembers() {
        val s = FileReader("./data/recall_members.json").readText()
        members = if (s.isBlank())
            mutableListOf()
        else
            Gson().fromJson<List<RecallMember>>(s, object : TypeToken<List<RecallMember>>() {}.type)
                .toMutableList()
    }

    private fun isInBlacklist(member: Member): Boolean {
        if (members == null) loadMembers()
        val index = findMember(member) ?: return false
        val recallCount = members?.get(index)?.recallCount ?: 0
        return recallCount > MAX_RECALL_COUNT || recallCount == -1
    }

    private fun checkRecallCount(author: Member, operator: Member?) {
        // 自己主动撤回的才会被记录
        if (author.id != operator?.id) return
        val index = findMember(author)
        if (index == null) {  // 首次撤回，添加记录
            addMember(author, 1)
        } else if (members?.get(index)?.recallCount ?: 0 > 0) {
            updateMember(index)
        }
    }

    private fun updateMember(index: Int) {
        members?.get(index)?.let {
            it.recallCount++
        }
        save()
    }

    private fun findMember(member: Member): Int? {
        val index = members?.indexOfFirst {
            it.qqId == member.id && it.groupId == member.group.id
        }
        return if (-1 == index) null else index
    }

    override suspend fun invoke(event: GroupEvent) {
        if (members == null) loadMembers()
        (event as MessageRecallEvent.GroupRecall).run {
            checkRecallCount(author, operator)
            if (antiRecall[this.group.id] != true) return
            if (this.group.isValidGroup() && isInBlacklist(author)) {
                val name = author.nameCardOrNick
                val time = formatTime(messageTime)
                val message = cache[messageId] ?: return
                this.group.sendMessage(PlainText("$name $time 撤回了一条消息：\n").plus(message))
            }
        }
    }
}