package com.yumi.kotlin.actions

import com.google.gson.Gson
import com.yumi.kotlin.data.RobotResp
import com.yumi.kotlin.isRobot
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import net.mamoe.mirai.message.GroupMessageEvent
import java.io.FileReader

object AtBotAction : MessageHandler {

    override suspend fun invoke(event: GroupMessageEvent) {
        event.run {
            if (this.sender.isRobot()) return@run  //  屏蔽机器人消息
            print("atBot invoke\n")
            val content = this.message.last().toString()
            if (content.isBlank()) {
                FileReader("./data/introduction.txt").use {
                    reply(it.readText())
                }
                return@run
            }
            val url = "https://api.ownthink.com/bot?appid=xiaosi&spoken=$content"
            println(url)
            try {
                val resp = HttpClient(OkHttp).get<String>(url)
                val robot: RobotResp = Gson().fromJson(resp, RobotResp::class.java)
                if (robot.message == "success")
                    quoteReply(robot.data.info.text.replace("小思", "小拉面"))
                else
                    quoteReply("这个问题我还不会回答啦~~")
            } catch (e: Exception) {
                e.printStackTrace()
                quoteReply("我好像哪里发生了什么错误呢~~")
            }
        }
    }
}