import com.otonashi.RelayMessage
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain

object RelayMessageCommand: SimpleCommand(
    owner = RelayMessage,
    "relay",
    "转发",
    description = "转发用户消息指令",
) {
    @Handler
    suspend fun MemberCommandSenderOnMessage(context:CommandSender, target: String, vararg messages: Message) {
        val bot = context.bot
        val contact = bot?.getGroup(context.subject!!.id)
        val tempalte = buildMessageChain {
            +PlainText("${contact?.name}\n${context.name} 曰：\n");
            for((index, item) in messages.withIndex()) {
                +item;
                if(index != messages.size - 1) {
                    +PlainText(" ");
                }
            }
        }
        for(group in bot!!.groups) {
            if((group.name == target || group.id.toString() == target) && group.id != contact?.id) {
                group.sendMessage(tempalte);
                context.sendMessage("消息发送成功");
                break;
            }
        }
    }
}

