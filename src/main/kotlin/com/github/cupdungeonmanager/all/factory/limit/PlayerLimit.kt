package com.github.cupdungeonmanager.all.factory.limit

import com.github.cupdungeonmanager.all.api.events.player.PlayerLimitEvents
import com.github.cupdungeonmanager.all.storage.Storage
import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync

class PlayerLimit(val player: Player) {


    fun reset() {
        PlayerLimitEvents.Clear(player, null).call()
        submitAsync {
            Storage.INSTANCE.resetLimit(player)
        }
    }

    fun add(number: Int, dungeon: String) {
        PlayerLimitEvents.Add(player, number, dungeon).call()
        submitAsync {
            val now = Storage.INSTANCE.getLimit(player, dungeon)
            Storage.INSTANCE.setLimit(player, now + number, dungeon)
        }
    }

    fun reduce(number: Int, dungeon: String) {
        PlayerLimitEvents.Reduce(player, number, dungeon).call()
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
        PlayerLimitEvents.Set(player, number, dungeon).call()
        submitAsync {
            Storage.INSTANCE.setLimit(player, number, dungeon)
        }
    }

    fun clear(dungeon: String) {
        PlayerLimitEvents.Clear(player, dungeon).call()
        submitAsync {
            Storage.INSTANCE.clearLimit(player, dungeon)
        }
    }

}