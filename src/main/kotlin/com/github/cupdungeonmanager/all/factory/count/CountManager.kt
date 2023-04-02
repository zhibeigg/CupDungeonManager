package com.github.cupdungeonmanager.all.factory.count

import com.github.cupdungeonmanager.CupDungeonManager.config
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.serverct.ersha.dungeon.DungeonPlus
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.getMeta
import taboolib.platform.util.sendLang

object CountManager {

    val DungeonsReviveFreeTimes = mutableMapOf<String, Int>()
    val DungeonsReviveLimit = mutableMapOf<String, Int>()

    val move = mutableListOf<Player>()

    @Awake(LifeCycle.ACTIVE)
    fun load() {
        DungeonPlus.dungeonManager.getDungeons().forEach {
            DungeonsReviveFreeTimes[it.dungeonName] = config.getInt("DungeonsReviveFreeTimes.${it.dungeonName}")
            DungeonsReviveLimit[it.dungeonName] = config.getInt("DungeonsReviveLimit.${it.dungeonName}")
        }
    }

    @SubscribeEvent
    fun e(e: PlayerMoveEvent) {
        move.add(e.player)
    }


    @SubscribeEvent
    fun e(e: PlayerGameModeChangeEvent) {
        val player = e.player
        val world = player.world
        val manager = DungeonPlus.dungeonManager
        if (manager.isDungeonWorld(world)) {
            if (e.newGameMode == GameMode.SPECTATOR) {
                val ui = CountUI(player)
                ui.open()
                ui.mubei()
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEntityEvent) {
        val player = e.player
        val world = player.world
        val manager = DungeonPlus.dungeonManager
        if (manager.isDungeonWorld(world)) {
            val team = e.rightClicked.getMeta("team").toString()
            val death = Bukkit.getPlayerExact(team) ?: return
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