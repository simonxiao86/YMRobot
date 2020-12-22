package com.yumi.kotlin.actions

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.yumi.kotlin.data.Stock
import com.yumi.kotlin.data.StockInfo
import com.yumi.kotlin.data.StockSearchResp
import com.yumi.kotlin.util.dropLetters
import com.yumi.kotlin.util.isEnglishOrDigit
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.*

class StockAction : MessageHandler {
    override suspend fun invoke(event: GroupMessageEvent) {
        event.apply {
            val msg = event.message[PlainText]?.content
            val keyWord = msg?.drop(msg.lastIndexOf(" "))?.trim()
            val resp = search(keyWord)
            if (resp.isNullOrBlank())
                quoteReply("未查询到相关数据")
            else
                quoteReply("为您查询到：\n$resp")
        }
    }

    private suspend fun search(keyWord: String?): String? {
        if (keyWord.isNullOrEmpty()) return null
        var key = keyWord
        if (keyWord.isEnglishOrDigit())
            key = keyWord.dropLetters()
        val resp = HttpClient(OkHttp).get<String>(
            "http://searchapi.eastmoney.com/bussiness/web/QuotationLabelSearch?token=P7469MH69L6SWUWWFKKXTAD27SUQJ4H9&keyword=$key&type=0&pi=1&ps=30"
        )
        val json = Gson().fromJson<StockSearchResp>(resp, StockSearchResp::class.java)
        if (json.Data.isNullOrEmpty()) return null
        if (json.Data.size == 1 && json.Data[0].Datas.size == 1) {
            // 只有一条数据，直接去查详情
            return getDetail(json.Data[0].Type, json.Data[0].Datas[0])
        }
        return generateSearchResult(json.Data)
    }

    private fun generateSearchResult(data: List<Stock>): String? {
        val filter = data.filter {
            it.Type == 1 || it.Type == 2 || it.Type == 8
        }
        return filter.joinToString("\n") {
            val child = it.Datas.map { info ->
                "${info.Name}（${info.Code}）"
            }.take(5).joinToString("、")
            it.Name + "：\n" + child
        }
    }

    private suspend fun getDetail(type: Int, stock: StockInfo): String? {
        val trueCode = stock.MktNum + "." + stock.Code
        val resp = HttpClient(OkHttp).get<String>(
            "http://push2.eastmoney.com/api/qt/stock/get?secid=$trueCode"
        )
        var json = JsonParser.parseString(resp).asJsonObject.get("data")
        if (json.isJsonObject)
            json = json.asJsonObject
        else
            return null
        var desc = generateResult(type, json)
        val url = when (stock.MarketType) {
            "1" -> "http://quote.eastmoney.com/sh${stock.Code}.html"
            "2" -> "http://quote.eastmoney.com/sz${stock.Code}.html"
            "6" -> "http://fund.eastmoney.com/${stock.Code}.html"
            else -> null
        }
        url?.let {
            desc += "\n详情请点击：$it"
        }
        return desc
    }

    private fun generateResult(type: Int, json: JsonObject): String {
        return when (type) {
            1 -> {  // AB股
                """${json["f58"]} ${json["f57"]} ${json["f43"]}
                    |今开：${json["f46"]} 昨收：${json["f60"]}
                    |最高：${json["f44"]} 最低：${json["f45"]}
                    |涨停：${json["f51"]} 跌停：${json["f52"]}
                    |换手：${json["f168"]} 量比：${json["f50"]}
                    |成交量：${json["f47"]} 成交额：${json["f48"]}
                    |市盈（动）：${json["f162"]} 市净：${json["f167"]}
                    |总市值：${json["f116"]} 流通市值：${json["f117"]}
                """.trimMargin()
            }
            2 -> {  // 指数
                """${json["f58"]} ${json["f57"]} ${json["f43"]}
                    |今开：${json["f46"]} 昨收：${json["f60"]}
                    |最高：${json["f44"]} 最低：${json["f45"]}
                    |涨跌幅：${json["f169"]} 涨跌额：${json["f170"]}
                    |换手：${json["f168"]} 振幅：${json["f171"]}
                    |成交量：${json["f47"]} 成交额：${json["f48"]}
                """.trimMargin()
            }
            8 -> {  // 基金
                """${json["f58"]} ${json["f57"]} ${json["f43"]}
                    |今开：${json["f46"]} 昨收：${json["f60"]}
                    |最高：${json["f44"]} 最低：${json["f45"]}
                    |涨停：${json["f51"]} 跌停：${json["f52"]}
                    |换手：${json["f168"]} 量比：${json["f50"]}
                    |成交量：${json["f47"]} 成交额：${json["f48"]}
                    |振幅：${json["f171"]} 均价：${json["f71"]}
                    |外盘：${json["f49"]} 内盘：${json["f161"]}
                """.trimMargin()
            }
            else -> ""
        }
    }
}