package com.github.cupdungeonmanager.all.command

import com.github.cupdungeonmanager.all.factory.count.PlayerCount
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

@CommandHeader("count", permission = "CupDungeonManager.command.count")
object CommandCount {

    @CommandBody
    val helper = mainCommand {
        createHelper()
    }

    @CommandBody
    val reduce = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
            dynamic("value") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                    val factory = PlayerCount(playerExact)
                    sender.sendLang("count-reduce", playerExact.displayName, factory.get(), argument.toInt())
                    factory.reduce(argument.toInt())
                }
            }
        }
    }

    @CommandBody
    val add = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
            dynamic("value") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                    val factory = PlayerCount(playerExact)
                    sender.sendLang("count-add", playerExact.displayName, factory.get(), argument.toInt())
                    factory.add(argument.toInt())
                }
            }
        }
    }

    @CommandBody
    val set = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
            dynamic("value") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                    val factory = PlayerCount(playerExact)
                    factory.set(argument.toInt())
                    sender.sendLang("count-set", playerExact.displayName, argument.toInt())
                }
            }
        }
    }

    @CommandBody
    val addAll = subCommand {
        dynamic("value") {
            execute<ProxyCommandSender> { _, _, argument ->
                val player = Bukkit.getOnlinePlayers()
                player.forEach {
                    val factory = PlayerCount(it)
                    factory.add(argument.toInt())
                    it.sendLang("count-add", it.displayName, factory.get(), argument.toInt())
                }
            }
        }
    }



}