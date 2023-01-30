package com.github.cupdungeonmanager.limit

import com.github.cupdungeonmanager.limit.storage.Storage
import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync

class PlayerDailyLimit(val player: Player) {


    fun reset() {
        submitAsync {
            Storage.INSTANCE.resetLimit(player)
        }
    }

    fun add(number: Int, dungeon: String) {
        submitAsync {
            val now = Storage.INSTANCE.getLimit(player, dungeon)
            Storage.INSTANCE.setLimit(player, now + number, dungeon)
        }
    }

    fun reduce(number: Int, dungeon: String) {
        submitAsync {
            val now = Storage.INSTANCE.getLimit(player, dungeon)
            if (now <= number) {
                Storage.INSTANCE.setLimit(player, 0, dungeon)
            } else {
                Storage.INSTANCE.setLimit(player, now - number, dungeon)
            }
        }
    }

    fun set(number: Int, dungeon: String) {
        submitAsync {
            Storage.INSTANCE.setLimit(player, number, dungeon)
        }
    }

    fun clear(dungeon: String) {
        submitAsync {
            Storage.INSTANCE.clearLimit(player, dungeon)
        }
    }

}