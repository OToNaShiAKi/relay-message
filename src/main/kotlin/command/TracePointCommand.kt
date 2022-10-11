import com.otonashi.RelayMessage
import com.otonashi.data.GetTracePoint
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain


object TracePointCommand: CompositeCommand(
    owner = RelayMessage,
    "trace",
    "绑定",
    description = "绑定群聊直播间",
) {
    @SubCommand("combine", "绑定")
    suspend fun MemberCommandSenderOnMessage.combine(liveid: String) {
        val id = subject.id.toString()
        val rooms = TracePointData.LiveRooms.getOrDefault(id, mutableMapOf())
        if(!rooms.contains(liveid)) rooms.put(liveid, mutableListOf())
        TracePointData.LiveRooms.put(id, rooms)
        sendMessage("成功绑定直播间：$liveid")
    }
    @SubCommand("remove", "解绑")
    suspend fun MemberCommandSenderOnMessage.remove(liveid: String) {
        val id = subject.id.toString()
        val rooms = TracePointData.LiveRooms.getOrDefault(id, mutableMapOf()).filter { it.key !== liveid }
        TracePointData.LiveRooms.put(id, rooms as MutableMap)
        sendMessage("成功解绑直播间：$liveid")
    }
    @SubCommand("point", "打点")
    suspend fun MemberCommandSenderOnMessage.point(liveid: String = "") {
        val id = subject.id.toString()
        val rooms = TracePointData.LiveRooms.getOrDefault(id, mutableMapOf()).filter { it.key.equals(liveid) || liveid.isNullOrEmpty() }
        val template = GetTracePoint(rooms as Map<String, MutableList<String>>)
        subject.sendMessage(template)
    }
    @SubCommand("lists", "记录")
    suspend fun MemberCommandSenderOnMessage.lists(liveid: String = "") {
        val id = subject.id.toString()
        val rooms = TracePointData.LiveRooms.getOrDefault(id, mutableMapOf()).filter { it.key.equals(liveid) || liveid.isNullOrEmpty() }
        val template = buildMessageChain {
            for(item in rooms) {
                +PlainText("${item.key}：\n  ")
                +PlainText(item.value.joinToString("\n  ") + "\n")
            }
            if (this.isEmpty()) +PlainText("未绑定直播间或无任何记录")
        }
        subject.sendMessage(template)
    }
    @SubCommand("clear", "清除")
    suspend fun MemberCommandSenderOnMessage.clear(liveid: String = "") {
        val id = subject.id.toString()
        val rooms = TracePointData.LiveRooms.getOrDefault(id, mutableMapOf()).filter { it.key.equals(liveid) || liveid.isNullOrEmpty() }
        for(item in rooms) {
            item.value.clear()
        }
        subject.sendMessage("清除完毕")
    }
}

