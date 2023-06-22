package com.github.cupdungeonmanager.all.api

import com.github.cupdungeonmanager.all.factory.count.CountManager.playerData
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object CupDungeonManagerAPI {

    /**
     * 获取所有在线玩家的墓碑
     *
     * @param Player 玩家
     * @param Entity 墓碑
     */
    fun getAllMuBei(): Map<Player, Entity?> {
        val mubei = mutableMapOf<Player, Entity?>()
        playerData.map { map ->
            Bukkit.getPlayerExact(map.key)?.let { mubei[it] = map.value.mubei }
        }
        return mubei
    }


}