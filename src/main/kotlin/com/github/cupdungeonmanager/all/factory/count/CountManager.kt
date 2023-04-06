package com.github.cupdungeonmanager.all.factory.count

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
import taboolib.platform.util.sendLang

object CountManager {

    private val DungeonsReviveFreeTimes = mutableMapOf<String, Int>()
    private val DungeonsReviveLimit = mutableMapOf<String, Int>()

    val freeRevive = mutableMapOf<String, Int>()
    val noFreeRevive = mutableMapOf<String, Int>()

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
                ui.mubei()
                submitAsync {
                    Thread.sleep(200)
                    sync { ui.open() }
                }
                debug("mode_change")
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEntityEvent) {
        val player = e.player
        val world = player.world
        val manager = DungeonPlus.dungeonManager
        if (manager.isDungeonWorld(world)) {
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

    fun revive(death: Player, mubei: Entity, player: Player) {
        death.gameMode = GameMode.SURVIVAL
        death.teleport(mubei)
        DungeonPlus.teamManager.getTeam(player)?.players?.forEach {
            Bukkit.getPlayer(it)?.sendLang("team-help-other", death.displayName, player.displayName)
        }
    }

}