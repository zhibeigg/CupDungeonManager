package com.github.cupdungeonmanager.all.command

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.all.api.CupDungeonManagerAPI
import com.github.cupdungeonmanager.all.api.events.PluginReloadEvent
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

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
    val mubei = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("special") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                    CupDungeonManagerAPI.setSpecialMuBei(playerExact, argument)
                    sender.sendMessage("设置成功, 当前特殊墓碑为, ${CupDungeonManagerAPI.getSpecialMuBei(playerExact)}")
                    playerExact.sendLang("mubei-set", argument)
                }
            }
        }
    }

    @CommandBody
    val reload = subCommand {

        execute<ProxyCommandSender> { sender, _, _ ->
            config.reload()
            PluginReloadEvent().call()
            sender.sendMessage("重载成功")
        }

    }

}