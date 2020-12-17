package com.yumi.kotlin.actions

import com.google.gson.Gson
import com.yumi.kotlin.data.EmojiResp
import com.yumi.kotlin.util.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.PlainText
import java.io.File
import java.lang.Exception

object FansAction : MessageHandler {

//    // 群号：message内容
//    private val cache: HashMap<Long, MessageChain> = hashMapOf()
//
//    // 群号：是否锁定
//    private var isLock: HashMap<Long, Boolean> = hashMapOf()

    private suspend fun specialDeal(event: GroupMessageEvent): Boolean {
        event.run {
            containsAny("好厉害", "大佬", "牛逼", "好棒").yes {
                val image = when {
                    containsAny("低调", "龙哥") -> {
                        File("asset/didiao.jpg").uploadAsImage()
                    }
                    containsAny("群主", "200", "法老", "法法", "法海") -> {
                        File("asset/200.png").uploadAsImage()
                    }
                    containsAny("alex", "aa", "a少", ignoreCase = true) -> {
                        File("asset/Alex.jpg").uploadAsImage()
                    }
                    containsAny("大宝", ignoreCase = true) -> {
                        File("asset/dabao.png").uploadAsImage()
                    }
                    containsAny("星星", ignoreCase = true) -> {
                        File("asset/xing.png").uploadAsImage()
                    }
                    else -> {
                        null
                    }
                }
                image?.let {
                    70.simpleRate {
                        reply(it)
                    }
                }
                return image != null
            }
            return false
        }
    }

    override suspend fun invoke(event: GroupMessageEvent) {
        event.apply {
            if (specialDeal(this)) return
            30.simpleRate {
                emoji(event)
            }

//            val msg = message[PlainText]?.content ?: ""
//            val last = cache[group.id]?.get(PlainText)?.content
//            // 空消息不作处理
//            if (msg.isBlank()) return
//            // 和上一条消息内容相同，且未被锁定，则触发表情包模式
//            if (last?.isNotBlank() == true
//                && last == msg
//                && isLock[group.id] == false
//            ) {
//                println("和上一条消息相同，且未被锁定，则触发表情包模式，last = $last, msg = $msg")
//                isLock[group.id] = true
//                emoji(event)
//            }
//            // 夹杂了不同消息后解锁
//            if (last != msg) {
//                println("夹杂了不同消息后解锁，last = $last, msg = $msg")
//                isLock[group.id] = false
//            }
//            // 重新赋值
//            cache[group.id] = message
        }
    }

    private suspend fun emoji(event: GroupMessageEvent) {
        event.run {
            if (this.outOfLength(10)) return@run
            val type = when {
                containsAny("666", "鼓掌", "大佬") -> 109
                containsAny("加油") -> 112
                containsAny("记仇", "小本本") -> 100
                containsAny("好帅", "太帅", "真帅") -> 38
                containsAny("好骚", "太骚", "真骚") -> 39
                containsAny("开车", "上车") -> 36
                containsAny("辣鸡", "垃圾") -> 65
                containsAny("好厉害", "大佬", "牛逼", "好棒") -> 90
                else -> null
            }
            type?.let { sendEmojiPic(it, message[PlainText]?.content ?: "", this) }
        }
    }

    /**
     * @params type 生成的表情包类型
     *  109: 鼓掌
     *  112: 加油
     *  100: 记仇
     *  38: 好帅
     *  39: 好骚
     *  36: 开车
     *  90: 社会社会
     *  65: 辣鸡
     */
    private suspend fun sendEmojiPic(type: Int, text: String, event: GroupMessageEvent) {
        try {
            val resp = HttpClient(OkHttp).submitFormWithBinaryData<String>(
                "https://www.52doutu.cn/api/", formData {
                    append("types", "maker")
                    append("id", type)
                    append("str1", text)
                    append("str2", "hei")
                })
            val url = Gson().fromJson(resp, EmojiResp::class.java).url
            event.sendImage(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}