package com.github.cupdungeonmanager.all.api.events.player

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerCountEvents(player: Player, value: Int) : BukkitProxyEvent() {

    class Set(player: Player, value: Int) : BukkitProxyEvent()

    class Add(player: Player, value: Int) : BukkitProxyEvent()

    class Reduce(player: Player, value: Int) : BukkitProxyEvent()

    class Clear(player: Player) : BukkitProxyEvent()

}