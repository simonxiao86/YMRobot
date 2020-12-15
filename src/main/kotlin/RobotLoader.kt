package com.yumi.kotlin

import com.google.gson.JsonParser
import com.yumi.kotlin.actions.GroupListenerHost
import com.yumi.kotlin.actions.MessageSubscriber
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.registerEvents
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.join
import java.io.FileReader


// 957968887：测试群
// 656238669：糖果小群
// 181398081：FlutterCandies
// 892398530：OpenFlutter
fun Group.isValidGroup(): Boolean = id == 957968887L || id == 656238669L || id == 892398530L || id == 181398081L

// 776575158：糖果小蜜
// 3090077983：糖果小爷
// 2300406668：糖果小宝
fun Member.isRobot(): Boolean = id == 776575158L || id == 3090077983L || id == 2300406668L

suspend fun main() {
    val json = FileReader("./src/main/config.json").use {
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