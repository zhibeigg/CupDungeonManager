package com.github.cupdungeonmanager.all.api.events.player

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerLimitEvents() {

    class Set(player: Player, value: Int, dungeon: String) : BukkitProxyEvent()

    class Add(player: Player, value: Int, dungeon: String) : BukkitProxyEvent()

    class Reduce(player: Player, value: Int, dungeon: String) : BukkitProxyEvent()

    //null为清除所有副本的次数
    class Clear(player: Player, dungeon: String?) : BukkitProxyEvent()

    class Reset() : BukkitProxyEvent()


}