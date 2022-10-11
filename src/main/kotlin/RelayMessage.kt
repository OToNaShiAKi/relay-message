package com.otonashi

import FindUsersCommand
import RelayMessageCommand
import TracePointCommand
import com.otonashi.data.GetTracePoint

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.utils.info




object RelayMessage : KotlinPlugin(
    JvmPluginDescription(
        id = "com.otonashi.relay-message",
        name = "relay-message",
        version = "0.1.0",
    ) {
        author("HasuteruYusumi")
    }
) {
    override fun onEnable() {
        CommandManager.registerCommand(RelayMessageCommand)
        CommandManager.registerCommand(FindUsersCommand)
        CommandManager.registerCommand(TracePointCommand)

        TracePointData.reload()

        GlobalEventChannel.subscribeAlways<NudgeEvent> { event ->
            if(event.target === bot) {
                val rooms = TracePointData.LiveRooms.getOrDefault(subject.id.toString(), mutableMapOf())
                val template = GetTracePoint(rooms as Map<String, MutableList<String>>)
                subject.sendMessage(template)
            }
        }
        logger.info { "Plugin loaded" }
    }
}