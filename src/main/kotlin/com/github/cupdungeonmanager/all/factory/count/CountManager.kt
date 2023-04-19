package com.github.cupdungeonmanager.all.factory.count

import com.germ.germplugin.api.KeyType
import com.germ.germplugin.api.event.GermKeyDownEvent
import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.CupDungeonManager.debug
import com.github.cupdungeonmanager.all.api.events.PluginReloadEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.*
import org.serverct.ersha.dungeon.DungeonPlus
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.sync
import taboolib.platform.util.sendActionBar
import taboolib.platform.util.sendLang

object CountManager {

    private val DungeonsReviveFreeTimes = mutableMapOf<String, Int>()
    private val DungeonsReviveLimit = mutableMapOf<String, Int>()

    val freeRevive = mutableMapOf<String, Int>()
    val noFreeRevive = mutableMapOf<String, Int>()

    val playerData = mutableMapOf<String, CountUI>()

    private val move = mutableSetOf<Player>()

    @Awake(LifeCycle.ACTIVE)
    fun load() {
        DungeonsReviveLimit.clear()
        DungeonsReviveFreeTimes.clear()
        DungeonPlus.contentManager.content.forEach { (_, dungeon) ->
            DungeonsReviveFreeTimes[dungeon.dungeonName] = config.getInt("DungeonsReviveFreeTimes.${dungeon.dungeonName}", 0)
            DungeonsReviveLimit[dungeon.dungeonName] = config.getInt("DungeonsReviveLimit.${dungeon.dungeonName}", 999)
            debug(dungeon.dungeonName + config.getInt("DungeonsReviveFreeTimes.${dungeon.dungeonName}") + "|" + config.getInt("DungeonsReviveLimit.${dungeon.dungeonName}"))
        }
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        noFreeRevive.remove(e.player.name)
        freeRevive.remove(e.player.name)
        move.remove(e.player)
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        load()
    }

    @SubscribeEvent
    fun e(e: PlayerMoveEvent) {
        if (!move.contains(e.player)) {
            move.add(e.player)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerChangedWorldEvent) {
        val dp = DungeonPlus.dungeonManager
        val player = e.player
        if (dp.isDungeonWorld(player.world)) {
            val dungeon = dp.getDungeon(player.world) ?: return
            freeRevive[player.name] = DungeonsReviveFreeTimes[dungeon.dungeonName] ?: 0
            noFreeRevive[player.name] = DungeonsReviveLimit[dungeon.dungeonName] ?: 999
            debug(player.name + noFreeRevive[player.name] + "|" + freeRevive[player.name])
        } else {
            playerData.remove(player.name)
            freeRevive.remove(player.name)
            noFreeRevive.remove(player.name)
            debug(player.name + noFreeRevive[player.name] + "|" + freeRevive[player.name])
        }
    }


    @SubscribeEvent
    fun e(e: PlayerGameModeChangeEvent) {
        val player = e.player
        val world = player.world
        val manager = DungeonPlus.dungeonManager
        if (manager.isDungeonWorld(world)) {
            if (e.newGameMode == GameMode.SPECTATOR) {
                val ui = CountUI(player, noFreeRevive[player.name] ?: 0, freeRevive[player.name] ?: 0)
                playerData[player.name] = ui
                submitAsync {
                    Thread.sleep(200)
                    sync {
                        ui.open()
                        ui.mubei()
                    }
                }
                debug("mode_change")
            }
        }
    }

    @SubscribeEvent
    fun e(e: GermKeyDownEvent) {
        val player = e.player
        val dp = DungeonPlus.dungeonManager
        if (dp.isDungeonWorld(player.world)) {
            val team = getTeamPlayer(player)
            if (player.gameMode == GameMode.SPECTATOR) {
                when (e.keyType) {
                    KeyType.KEY_LEFT -> {
                        var newTarget: Player = team.first()
                        team.forEachIndexed { index, it ->
                            val target = it.spectatorTarget
                            if (target != null) {
                                if (it.name == target.name) {
                                    newTarget = if (index == 0) {
                                        team.last()
                                    } else {
                                        team[index - 1]
                                    }
                                }
                            }
                        }
                        player.spectatorTarget = newTarget
                        player.sendActionBar(newTarget.displayName)
                    }

                    KeyType.KEY_RIGHT -> {
                        var newTarget: Player = team.first()
                        team.forEachIndexed { index, it ->
                            val target = it.spectatorTarget
                            if (target != null) {
                                if (it.name == target.name) {
                                    newTarget = if (index == team.size + 1) {
                                        team.first()
                                    } else {
                                        team[index + 1]
                                    }
                                }
                            }
                        }
                        player.spectatorTarget = newTarget
                        player.sendActionBar(newTarget.displayName)
                    }

                    KeyType.KEY_SPACE -> {
                        val ui = playerData[player.name] ?: CountUI(player, noFreeRevive[player.name] ?: 0, freeRevive[player.name] ?: 0)
                        playerData[player.name] = ui
                        ui.open()
                    }

                    else -> return
                }
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEntityEvent) {
        val player = e.player
        val world = player.world
        val manager = DungeonPlus.dungeonManager
        if (manager.isDungeonWorld(world)) {
            debug(e.rightClicked.getMetadata("CupDungeonManager:Team").getOrNull(0)?.value().toString() + "|mubei")
            val death = e.rightClicked.getMetadata("CupDungeonManager:Team").getOrNull(0)?.value() as? Player ?: return
            debug("${player}${death}, click mubei")
            if (player == death) {
                player.sendLang("do-not-interact-self")
                return
            }
            submitAsync {
                move.remove(player)
                player.sendTitle("请勿移动!等待三秒", "", 5, 10, 5)
                Thread.sleep(1000)
                if (move.contains(player)) {
                    player.sendTitle("你移动了", "", 5, 10, 5)
                    move.remove(player)
                    cancel()
                }
                player.sendTitle("请勿移动!等待两秒", "", 5, 10, 5)
                Thread.sleep(1000)
                if (move.contains(player)) {
                    player.sendTitle("你移动了", "", 5, 10, 5)
                    move.remove(player)
                    cancel()
                }
                player.sendTitle("请勿移动!等待一秒", "", 5, 10, 5)
                Thread.sleep(1000)
                if (move.contains(player)) {
                    player.sendTitle("你移动了", "", 5, 10, 5)
                    move.remove(player)
                    cancel()
                }
                revive(death, e.rightClicked, player)
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerCommandPreprocessEvent) {
        val player = e.player
        if (!player.isOp) {
            if (DungeonPlus.dungeonManager.isDungeonWorld(player.world)) {
                if (e.message.contains("revive")) {
                    e.isCancelled = true
                    if (player.gameMode == GameMode.SPECTATOR) {
                        playerData[player.name]?.open()
                    }
                }
            }
        }
    }

    fun revive(death: Player, mubei: Entity, player: Player) {
        death.gameMode = GameMode.SURVIVAL
        death.teleport(mubei)
        DungeonPlus.teamManager.getTeam(player)?.players?.forEach {
            Bukkit.getPlayer(it)?.sendLang("team-help-other", death.displayName, player.displayName)
        }
    }

    fun getTeamPlayer(player: Player): MutableList<Player> {
        val players = mutableListOf<Player>()
        DungeonPlus.teamManager.getTeam(player)?.team?.players?.forEach {
            if (Bukkit.getPlayer(it)!! != player) {
                players.add(Bukkit.getPlayer(it)!!)
            }
        }
        return players
    }

}