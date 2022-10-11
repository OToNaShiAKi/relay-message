package com.otonashi.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.serialization.*
import java.time.format.DateTimeFormatter

val client = HttpClient(OkHttp) {
    defaultRequest {
        header(HttpHeaders.Origin, "https://live.bilibili.com")
        header(HttpHeaders.Referrer, "https://live.bilibili.com")
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    BrowserUserAgent()
}

@Serializable
data class BiliLiveReponse(val room_id: String, val live_status: Int, val live_time: Long)
@Serializable
data class BiliLiveData(val data: BiliLiveReponse)

suspend fun GetTracePoint(rooms: Map<String, MutableList<String>>):MessageChain {
    val template = buildMessageChain {
        for(item in rooms) {
            val response = client.get("https://api.live.bilibili.com/xlive/web-room/v2/index/getRoomPlayInfo") {
                parameter("room_id", item.key)
                parameter("protocol", "0,1")
                parameter("format", "0,1,2")
                parameter("codec", "0,1")
                parameter("qn", 0)
                parameter("platform", "web")
                parameter("ptype", 8)
                parameter("dolby", 5)
            }.body<BiliLiveData>().data
            if(response.live_status === 1) {
                val now = LocalDateTime.now()
                val time = now.toEpochSecond(ZoneOffset.of("+8")) - response.live_time
                val hour = (time / (60 * 60)).toInt()
                val minute = ((time - hour * 60 * 60) / 60).toInt()
                val point = "${now.monthValue}月${now.dayOfMonth}日 ${hour}h${minute}m"
                item.value.add(point)
                +PlainText("${response.room_id}：$point \n")
            }
        }
        if (this.isEmpty()) +PlainText("未绑定直播间或绑定直播间未开播")
    }
    return template
}