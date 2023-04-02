package com.github.cupdungeonmanager.all.factory.count

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.serverct.ersha.dungeon.DungeonPlus
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.getMeta
import taboolib.platform.util.sendLang

object CountManager {

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
            death.gameMode = GameMode.SURVIVAL
            death.teleport(e.rightClicked)
            DungeonPlus.teamManager.getTeam(player)?.players?.forEach {
                Bukkit.getPlayer(it)?.sendLang("team-help-other", death.displayName, player.displayName)
            }
        }
    }

}