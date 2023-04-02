package com.github.cupdungeonmanager.all.factory.count

import org.bukkit.GameMode
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.serverct.ersha.dungeon.DungeonPlus
import taboolib.common.platform.event.SubscribeEvent

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

}