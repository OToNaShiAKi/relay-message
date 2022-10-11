import com.otonashi.RelayMessage
import com.otonashi.data.GetTracePoint
import com.otonashi.data.client
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import kotlinx.serialization.Serializable
import net.mamoe.mirai.message.data.FileMessage
import net.mamoe.mirai.utils.ExternalResource
import java.io.File
import java.net.URI

@Serializable
data class SoftResponseAssets(val browser_download_url: String)
@Serializable
data class SoftResponse(val assets: Array<SoftResponseAssets>)

object DownloadCommand: CompositeCommand(
    owner = RelayMessage,
    "download",
    "绑定",
    description = "绑定群聊直播间",
) {
    @SubCommand("douden", "同传软件")
    suspend fun MemberCommandSenderOnMessage.combine(platform: String = "exe") {
        val url = "https://api.github.com/repos/OToNaShiAKi/material-douden-tool/releases/latest";
        val response:SoftResponse = client.get(url).body();
        for(item in response.assets) {
            if(item.browser_download_url.endsWith(platform)) {
                val filename = item.browser_download_url.split("/").last()
                val file = File("./$filename")
                client.get(url).bodyAsChannel().toInputStream().copyTo(file.outputStream())
//                subject.files.uploadNewFile(filename, )
            }
        }
    }
}

