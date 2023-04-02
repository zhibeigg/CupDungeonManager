package com.github.cupdungeonmanager.all.command

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.all.api.events.PluginReloadEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

@CommandHeader("CupManager", aliases = ["cm", "cdm"], permission = "CupDungeonManager.command")
object Command {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val limit = CommandLimit

    @CommandBody
    val count = CommandCount

    @CommandBody
    val reload = subCommand {

        execute<ProxyCommandSender> { sender, _, _ ->
            config.reload()
            PluginReloadEvent().call()
            sender.sendMessage("重载成功")
        }

    }



}