package com.yumi.kotlin.data

data class RobotResp(
    val message: String,
    val data: RobotBody
)

data class RobotBody(
    val type: Int,
    val info: Info
)

data class Info(val text: String)

data class Tree(val type: String, val path: String?, val url: String, val download_url: String)

data class NewsResp(val has_more: Boolean, val return_count: Int, val data: List<News>)

data class News(
    val title: String,
    val source_url: String,
    val item_id: String
)

data class RecallMember(
    val groupId: Long,
    val qqId: Long,
    val nickName: String,
    var recallCount: Int = 0
)

data class EmojiResp(
    val code: Int,
    val msg: String,
    val url: String
)

data class StockSearchResp(
    val Code: Int,
    val Data: List<Stock>?
)

data class Stock(
    val Type: Int,
    val Name: String,
    val Datas: List<StockInfo>
)

data class StockInfo(
    val Code: String,
    val Name: String,
    val MarketType: String,
    val MktNum: String
)

data class JueJinSearchRes(
    val key_word: String,
    val id_type: Int,
    val limit: Int,
    val search_type: Int
)