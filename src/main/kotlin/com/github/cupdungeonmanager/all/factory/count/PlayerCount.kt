package com.github.cupdungeonmanager.all.factory.count

import com.github.cupdungeonmanager.all.api.events.player.PlayerCountEvents
import com.github.cupdungeonmanager.all.storage.Storage
import org.bukkit.entity.Player

class PlayerCount(val player: Player) {

    fun add(number: Int) {
        PlayerCountEvents.Add(player, number).call()
        val now = Storage.INSTANCE.getCount(player)
        Storage.INSTANCE.setCount(player, now + number)
    }

    fun reduce(number: Int) {
        PlayerCountEvents.Reduce(player, number).call()
        val now = Storage.INSTANCE.getCount(player)
        if (now <= number) {
            Storage.INSTANCE.setCount(player, 0)
        } else {
            Storage.INSTANCE.setCount(player, now - number)
        }
    }

    fun set(number: Int) {
        PlayerCountEvents.Set(player, number).call()
        Storage.INSTANCE.setCount(player, number)
    }

    fun get() : Int {
        return Storage.INSTANCE.getCount(player)
    }


}