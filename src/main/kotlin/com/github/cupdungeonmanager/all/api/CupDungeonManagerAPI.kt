package com.github.cupdungeonmanager.all.api

import com.github.cupdungeonmanager.all.factory.count.CountManager.playerData
import com.github.cupdungeonmanager.all.storage.Storage
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

    /**
     * 获取玩家的墓碑
     *
     * @param Player 玩家
     */
    fun getMuBei(player: Player): Entity? {
        return playerData[player.name]?.mubei
    }


    /**
     * 获取玩家的特殊墓碑
     *
     * @param Player 玩家
     */
    fun getSpecialMuBei(player: Player): String {
        return Storage.INSTANCE.getMuBei(player)
    }

    /**
     * 设置玩家的特殊墓碑
     *
     * @param Player 玩家
     */
    fun setSpecialMuBei(player: Player, special: String) {
        return Storage.INSTANCE.setMuBei(player, special)
    }


}