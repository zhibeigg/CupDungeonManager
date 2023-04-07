package com.github.cupdungeonmanager.all

import com.github.cupdungeonmanager.all.storage.Storage
import org.bukkit.GameMode
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object Placeholder : PlaceholderExpansion {

    override val identifier: String = "CupDungeonManager"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return when (args) {
            "count" -> player?.let { Storage.INSTANCE.getCount(it).toString() } ?: "0"
            "limit" -> {
                val dungeon = args.split("_")[1]
                player?.let { Storage.INSTANCE.getLimit(it, dungeon).toString() } ?: "0"
            }
            "alive" -> if(player?.gameMode != GameMode.SPECTATOR) "&a存活" else "&4死亡"
            else -> "null"
        }
    }
}