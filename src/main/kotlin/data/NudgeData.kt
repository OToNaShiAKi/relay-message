import net.mamoe.mirai.console.data.*

object TracePointData : AutoSavePluginData("TracePointData") {
    @ValueDescription("绑定直播间")
    val LiveRooms: MutableMap<String, MutableMap<String, MutableList<String>>> by value()
}