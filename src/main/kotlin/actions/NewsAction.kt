package com.yumi.kotlin.actions

import com.google.gson.Gson
import com.yumi.kotlin.data.News
import com.yumi.kotlin.data.NewsResp
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.events.GroupEvent
import java.io.FileReader
import java.lang.Exception
import java.net.URLEncoder

class NewsAction : EventHandler {

    private val maxRetryCount = 10

    private val minNewsCount = 5
    private val defaultNewsCount = 10

    private var tryCount = 0

    override suspend fun invoke(event: GroupEvent) {
        try {
            tryCount = 0
            val resp = tryRequest()
            if (resp.isNotBlank()) {
                val news = Gson().fromJson<NewsResp>(resp, NewsResp::class.java).data
                val shortUrls = transformUrlToShort(news)
                val message = news.joinToString("\n") {
                    "${it.title}\n${shortUrls[it.item_id]}"
                }
                println(message)
                event.group.sendMessage("今日份早安新闻请查收：\n$message")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun tryRequest(): String {
        // 该头条 api 不稳定，可能返回空数据，此处设定最多重试 5 次
        try {
            tryCount++
            val resp = HttpClient(OkHttp).get<String>(
                "http://m.toutiao.com/list/?tag=news_tech&ac=wap&count=$defaultNewsCount&format=json_raw&as=479BB4B7254C150&cp=7E0AC8874BB0985"
            )
            if (tryCount < maxRetryCount) {
                if (resp.isBlank()) {
                    println("Api response is empty, retry! Has tried $tryCount times.")
                    return tryRequest()
                }
                val count = Gson().fromJson<NewsResp>(resp, NewsResp::class.java).return_count
                if (count < minNewsCount) {
                    println("Api response length is not enough, retry! Has tried $tryCount times.")
                    return tryRequest()
                }
            }
            return resp
        } catch (e: Exception) {
            println("Api error: ${e.message}, retry! Has tried $tryCount times.")
            e.printStackTrace()
            return tryRequest()
        }
    }

    private suspend fun transformUrlToShort(news: List<News>): Map<String, String> {
        val key = FileReader("./data/short_url_key.txt").readText()
        var urls = mapOf<String, String>()
        withContext(Dispatchers.IO) {
            urls = news.associate {
                val encodeUrl = URLEncoder.encode("https://www.toutiao.com${it.source_url}", "utf-8")
                val shortUrl = HttpClient(OkHttp).get<String>(
                    "http://api.3w.cn/api.htm?url=$encodeUrl&key=$key"
                )
                Pair(it.item_id, shortUrl)
            }
        }
        return urls
    }
}