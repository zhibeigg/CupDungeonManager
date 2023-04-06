package com.github.cupdungeonmanager.all.command

import com.github.cupdungeonmanager.CupDungeonManager
import com.github.cupdungeonmanager.all.factory.limit.LimitManager
import com.github.cupdungeonmanager.all.factory.limit.PlayerLimit
import com.github.cupdungeonmanager.all.storage.Storage
import org.bukkit.Bukkit
import org.serverct.ersha.dungeon.DungeonPlus
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
import taboolib.platform.util.sendLang

@CommandHeader("limit", permission = "CupDungeonManager.command.limit")
object CommandLimit {

    @CommandBody
    val helper = mainCommand {
        createHelper()
    }

    @CommandBody
    val reduce = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("dungeon") {
                suggestion<ProxyCommandSender> { _, _ -> DungeonPlus.contentManager.content.map { it.value.dungeonName } }

                dynamic("value") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                        PlayerLimit(playerExact).reduce(argument.toInt(), context["dungeon"])
                        sender.sendMessage("减少成功, 当前${Storage.INSTANCE.getLimit(playerExact, context["dungeon"])}")
                        playerExact.sendLang("limit-reduce", Storage.INSTANCE.getLimit(playerExact, context["dungeon"]))
                    }
                }
            }
        }
    }

    @CommandBody
    val add = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("dungeon") {
                suggestion<ProxyCommandSender> { _, _ -> DungeonPlus.contentManager.content.map { it.value.dungeonName } }

                dynamic("value") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                        PlayerLimit(playerExact).add(argument.toInt(), context["dungeon"])
                        sender.sendMessage("添加成功, 当前${Storage.INSTANCE.getLimit(playerExact, context["dungeon"])}")
                        playerExact.sendLang("limit-add", Storage.INSTANCE.getLimit(playerExact, context["dungeon"]))
                    }
                }
            }
        }
    }

    @CommandBody
    val set = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("dungeon") {
                suggestion<ProxyCommandSender> { _, _ -> DungeonPlus.contentManager.content.map { it.value.dungeonName } }

                dynamic("value") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                        PlayerLimit(playerExact).set(argument.toInt(), context["dungeon"])
                        sender.sendMessage("设置成功")
                        playerExact.sendLang("limit-set", Storage.INSTANCE.getLimit(playerExact, context["dungeon"]))
                    }
                }
            }
        }
    }

    @CommandBody
    val reset = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
            execute<ProxyCommandSender> { sender, _, argument ->
                val playerExact = Bukkit.getPlayerExact(argument) ?: return@execute
                PlayerLimit(playerExact).reset()
                sender.sendMessage("刷新成功")
            }
        }
    }

    @CommandBody
    val resetall = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            LimitManager.resetALL()
            sender.sendMessage("重新刷新全体成功")
        }
    }

    @CommandBody
    val look = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
            execute<ProxyCommandSender> { sender, _, argument ->
                val playerExact = Bukkit.getPlayerExact(argument) ?: return@execute
                PlayerLimit(playerExact).reset()
                sender.sendMessage("")
                CupDungeonManager.config.getConfigurationSection("limit")?.toMap()?.forEach {
                    val limit = Storage.INSTANCE.getLimit(playerExact, it.key)
                    sender.sendMessage("&e副本:${it.key}&7, &b次数:$limit".colored())
                }
                sender.sendMessage("")
            }
        }
    }

}