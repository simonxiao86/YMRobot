package com.yumi.kotlin

import com.google.gson.JsonParser
import com.yumi.kotlin.subscriber.GroupListenerHost
import com.yumi.kotlin.subscriber.MessageSubscriber
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.registerEvents
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.join
import java.io.FileReader


val allValidGroups by lazy {
    FileReader("./data/group.json").use {
        JsonParser.parseString(it.readText()).asJsonArray
    }.map {
        it.asJsonObject["groupId"].asLong
    }
}

val allRobots by lazy {
    FileReader("./data/robot.json").use {
        JsonParser.parseString(it.readText()).asJsonArray
    }.map {
        it.asJsonObject["robotId"].asLong
    }
}

fun Group.isValidGroup(): Boolean = allValidGroups.contains(this.id)

fun Member.isRobot(): Boolean = allRobots.contains(this.id)

suspend fun main() {
    val json = FileReader("./data/config.json").use {
        JsonParser.parseString(it.readText()).asJsonObject
    }
    val qqId = json["qq"].asLong //Bot的QQ号，需为Long类型，在结尾处添加大写L
    val password = json["pwd"].asString   //Bot的密码

    val ymBot = Bot(qqId, password) {
        // 覆盖默认的配置
        fileBasedDeviceInfo("device.json") // 使用 "device.json" 保存设备信息
    }.alsoLogin()  //新建Bot并登录

    ymBot.registerEvents(GroupListenerHost)
    ymBot.subscribeGroupMessages {
        MessageSubscriber.subscribe(this)
    }
    ymBot.join() // 等待 Bot 离线, 避免主线程退出
}