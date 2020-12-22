package com.yumi.kotlin.actions

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.yumi.kotlin.data.JueJinSearchRes
import com.yumi.kotlin.util.formatTimeToDay
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.PlainText

class JueJinAction : MessageHandler {
    override suspend fun invoke(event: GroupMessageEvent) {
        val keyWord = event.message[PlainText]?.content?.drop(8)
        if (keyWord.isNullOrBlank()) return
        val resp = HttpClient(OkHttp).post<String> {
            url("https://api.juejin.cn/search_api/v1/search")
            header("content-type", "application/json; charset=utf-8")
            body = Gson().toJson(
                JueJinSearchRes(
                    key_word = keyWord,
                    id_type = 2,
                    limit = 1,
                    search_type = 0
                )
            )
        }
        val json = JsonParser.parseString(resp).asJsonObject.get("data")
        println(json)
        if (json.isJsonArray) {
            val array = json.asJsonArray
            if (array.size() == 0) return
            val result = array[0].asJsonObject.getAsJsonObject("result_model")
            val article = result.getAsJsonObject("article_info")
            val author = result.getAsJsonObject("author_user_info")
            val tags = result.getAsJsonArray("tags").joinToString("、") {
                it.asJsonObject.get("tag_name").asString
            }
            event.reply(
                """
                    ${article.get("title").asString.take(17)}......
                    ${article.get("brief_content").asString.take(17)}......
                    发布于：${formatTimeToDay(article.get("ctime").asInt) ?: ""} ，阅读量：${article.get("view_count").asInt}
                    作者：${author.get("user_name").asString}
                    标签：${tags.take(15)}
                    https://juejin.im/post/${article.get("article_id").asString}
                """.trimIndent()
            )
        }
    }
}