package com.github.cupdungeonmanager.all.factory.count

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.CupDungeonManager.debug
import com.github.cupdungeonmanager.all.api.events.PluginReloadEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.serverct.ersha.dungeon.DungeonPlus
import org.serverct.ersha.dungeon.common.api.event.dungeon.DungeonEndEvent
import org.serverct.ersha.dungeon.common.api.event.dungeon.DungeonStartEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.sync
import taboolib.platform.util.sendLang

object CountManager {

    val DungeonsReviveFreeTimes = mutableMapOf<String, Int>()
    val DungeonsReviveLimit = mutableMapOf<String, Int>()

    val freeRevive = mutableMapOf<String, Int>()
    val noFreeRevive = mutableMapOf<String, Int>()

    private val move = mutableListOf<Player>()

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
    fun e(e: PluginReloadEvent) {
        load()
    }

    @SubscribeEvent
    fun e(e: PlayerMoveEvent) {
        move.add(e.player)
    }

    @SubscribeEvent
    fun e(e: DungeonStartEvent.Before) {
        e.dungeon.team.players.forEach {
            val player = Bukkit.getPlayer(it)!!
            freeRevive[player.name] = DungeonsReviveFreeTimes[e.dungeon.dungeonName] ?: 0
            noFreeRevive[player.name] = DungeonsReviveLimit[e.dungeon.dungeonName] ?: 999
        }
    }

    @SubscribeEvent
    fun e(e: DungeonEndEvent.After) {
        e.dungeon.team.players.forEach {
            val player = Bukkit.getPlayer(it)!!
            freeRevive.remove(player.name)
            noFreeRevive.remove(player.name)
        }
    }


    @SubscribeEvent
    fun e(e: PlayerGameModeChangeEvent) {
        val player = e.player
        val world = player.world
        val manager = DungeonPlus.dungeonManager
        if (manager.isDungeonWorld(world)) {
            if (e.newGameMode == GameMode.SPECTATOR) {
                val ui = CountUI(player)
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
            val team = e.rightClicked.getMetadata("team").getOrNull(0)?.asString() ?: return
            val death = Bukkit.getPlayerExact(team) ?: return
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