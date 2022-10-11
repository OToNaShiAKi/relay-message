import com.otonashi.RelayMessage
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain

object FindUsersCommand: SimpleCommand(
    owner = RelayMessage,
    "find",
    "查找",
    description = "寻找神龙",
) {
    @Handler
    suspend fun MemberCommandSenderOnMessage.handler(keyword: String) {
        val members = subject.members
        val template = buildMessageChain {
            for(item in members) {
                if (item.nameCardOrNick.contains(keyword)) {
                    +At(item.id)
                    +PlainText("\n")
                }
            }
            if (this.isEmpty()) +PlainText("你组没人了")
        }
        sendMessage(template)
    }
}

