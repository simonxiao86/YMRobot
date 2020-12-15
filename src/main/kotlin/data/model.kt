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