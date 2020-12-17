package com.yumi.kotlin.actions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yumi.kotlin.data.Tree
import com.yumi.kotlin.util.isImage
import com.yumi.kotlin.util.sendImage
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import net.mamoe.mirai.message.GroupMessageEvent
import java.io.FileReader
import kotlin.random.Random

class BoysAction : MessageHandler {

    override suspend fun invoke(event: GroupMessageEvent) {
        val boys = FileReader("./data/boys.json").use {
            Gson().fromJson<List<Tree>>(
                it.readText(), object : TypeToken<List<Tree>>() {}.type
            )
        }.filter {
            it.type == "dir"
        }
        val boy = boys[Random.nextInt(0, boys.size)]
        println("boy.url = ${boy.url}")
        try {
            val resp = HttpClient(OkHttp).get<String>(boy.url)
            val images = Gson().fromJson<List<Tree>>(
                resp, object : TypeToken<List<Tree>>() {}.type
            ).filter {
                it.path.isImage()
            }
            if (images.isEmpty()) return
            val path = images[Random.nextInt(0, images.size)].path
            val imagePath = "https://yumi0629.gitee.io/dress/${path}"
            event.sendImage(imagePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}