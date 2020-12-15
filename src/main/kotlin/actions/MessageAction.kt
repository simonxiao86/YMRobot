package com.yumi.kotlin.actions

import net.mamoe.mirai.event.GroupMessageSubscribersBuilder
import java.io.File


object MessageAction {

    fun invoke(builder: GroupMessageSubscribersBuilder) {
        builder.run {

            atBot {
                AtBotAction.invoke(this)
            }

            atBot().not().apply {

                print("common message\n")

                val isManager = newListeningFilter {
                    sender.permission.level == 1 || sender.permission.level == 2
                }
                startsWith("/recall").and(contains("on")).and(isManager).invoke {
                    GroupAction.setAntiRecallConfig(this.group.id, true)
                }
                startsWith("/recall").and(contains("off")).and(isManager).invoke {
                    GroupAction.setAntiRecallConfig(this.group.id, false)
                }
                startsWith("/女装大佬").invoke {
                    print("startsWith(\"/女装大佬\").invoke\n")
                    BoysAction.invoke(this)
                }
                containsAny("番号", "pornhub").invoke {
                    quoteReply("~~请不要开车哦，小蜜会逮捕你哒~~o(*^＠^*)o")
                }
                containsAny("sb", "傻逼", "fuck", "cnm", "bitch", "尼玛", "傻屌", "沙雕")
                    .and(newListeningFilter { this.message.last().toString().contains("http") }.not())
                    .invoke {
                        quoteReply("(*╹▽╹*)~争做文明糖果人，小面提醒你要文明用语哦~~")
                    }
                containsAny("几把", "叽霸", "jb")
                    .and(newListeningFilter { this.message.last().toString().contains("http") }.not())
                    .invoke {
                        quoteReply("(*╹▽╹*)~ 说 J 不带 B，争做文明糖果人，小面提醒你要文明用语哦~~")
                    }
                contains("发了一个“口令红包”").invoke {
                    quoteReply("(*╹▽╹*)~~亲亲，你涉嫌刷屏了哦，小蜜会逮捕你哒~~")
                }
                containsAny(
                    "龙哥好厉害", "低调好厉害", "低调低调", "低调大佬",
                    "龙哥牛逼", "低调牛逼"
                ).invoke {
                    val image = File("asset/didiao.jpg").uploadAsImage()
                    reply(image)
                }
                containsAny(
                    "群主好厉害", "法海好厉害", "200斤好厉害", "法老好厉害", "200好厉害", "法法好厉害",
                    "群主牛逼", "法海牛逼", "200斤牛逼", "法老牛逼", "200牛逼", "法法牛逼"
                ).invoke {
                    val image = File("asset/200.png").uploadAsImage()
                    reply(image)
                }
                containsAny(
                    "Alex好厉害", "alex好厉害", "AA好厉害",
                    "Alex牛逼", "alex牛逼", "AA牛逼", ignoreCase = true
                ).invoke {
                    val image = File("asset/Alex.jpg").uploadAsImage()
                    reply(image)
                }
            }

        }
    }

}