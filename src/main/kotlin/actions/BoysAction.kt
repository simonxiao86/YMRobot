package com.yumi.kotlin.actions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yumi.kotlin.data.Tree
import com.yumi.kotlin.isImage
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.uploadAsImage
import java.io.File
import java.io.FileReader
import kotlin.random.Random

object BoysAction {

    suspend fun invoke(event: GroupMessageEvent) {
        val boys = FileReader("./src/main/kotlin/data/boys.json").use {
            Gson().fromJson<List<Tree>>(it.readText(), object : TypeToken<List<Tree>>() {}.type)
        }.filter {
            it.type == "dir"
        }
        val boy = boys[Random.nextInt(0, boys.size)]
        println("boy.url = ${boy.url}")
        try {
            val resp = HttpClient(OkHttp).get<String>(boy.url)
            val images = Gson().fromJson<List<Tree>>(resp, object : TypeToken<List<Tree>>() {}.type).filter {
                it.path.isImage()
            }
            if (images.isEmpty()) return
            val downloadUrl = images[Random.nextInt(0, images.size)].download_url
            val accessToken = FileReader("./src/main/kotlin/data/gitee_token.txt").readText()
            val imagePath =
                "${downloadUrl}?access_token=$accessToken"
            val file = File("tmp/${System.currentTimeMillis()}")
            try {
                val byteArray = HttpClient(OkHttp).get<ByteArray>(imagePath)
                file.writeBytes(byteArray)
                var count = 0;
                while (count < 10) {
                    try {
                        val image = file.uploadAsImage(event.group)
                        event.reply(image)
                        break
                    } catch (e: Exception) {
                        count++
                    }
                }
            } finally {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}