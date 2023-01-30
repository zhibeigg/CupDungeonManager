package com.github.cupdungeonmanager

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.limit.LimitManager
import com.github.cupdungeonmanager.limit.PlayerDailyLimit
import com.github.cupdungeonmanager.limit.storage.Storage
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

@CommandHeader("CupLimit", aliases = ["limit", "cdm"], permission = "CupDungeonManager.command")
object Command {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val reload = subCommand {

        execute<ProxyCommandSender> { sender, _, _ ->
            config.reload()
            sender.sendMessage("重载成功")
        }

    }

    @CommandBody
    val reduce = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("dungeon") {
                suggestion<ProxyCommandSender> { _, _ -> DungeonPlus.dungeonManager.getDungeons().map { it.dungeonName } }

                dynamic("value") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                        PlayerDailyLimit(playerExact).reduce(argument.toInt(), context["dungeon"])
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
                suggestion<ProxyCommandSender> { _, _ -> DungeonPlus.dungeonManager.getDungeons().map { it.dungeonName } }

                dynamic("value") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                        PlayerDailyLimit(playerExact).add(argument.toInt(), context["dungeon"])
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
                suggestion<ProxyCommandSender> { _, _ -> DungeonPlus.dungeonManager.getDungeons().map { it.dungeonName } }

                dynamic("value") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val playerExact = Bukkit.getPlayerExact(context["player"]) ?: return@execute
                        PlayerDailyLimit(playerExact).set(argument.toInt(), context["dungeon"])
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
                PlayerDailyLimit(playerExact).reset()
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
                PlayerDailyLimit(playerExact).reset()
                sender.sendMessage("")
                config.getConfigurationSection("limit")?.toMap()?.forEach {
                    val limit = Storage.INSTANCE.getLimit(playerExact, it.key)
                    sender.sendMessage("&e副本:${it.key}&7, &b次数:$limit".colored())
                }
                sender.sendMessage("")
            }
        }
    }

}