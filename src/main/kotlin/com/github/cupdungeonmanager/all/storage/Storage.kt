package com.github.cupdungeonmanager.all.storage

import com.github.cupdungeonmanager.CupDungeonManager.config
import org.bukkit.entity.Player

interface Storage {

    companion object {

        val INSTANCE by lazy {
            when (type) {
                "LOCAL" -> StorageLocal()
                else -> error("找不到存储方式")
            }
        }

        val type: String
            get() = config.getString("database.use", "LOCAL")!!

    }

    /**刷新某人副本限制次数**/
    fun resetLimit(player: Player)

    /**清除副本限制次数**/
    fun clearLimit(player: Player, dungeon: String)

    /**设置副本限制次数**/
    fun setLimit(player: Player, number: Int, dungeon: String)

    /**获取副本限制次数**/
    fun getLimit(player: Player, dungeon: String) : Int

    /**刷新副本限制次数**/
    fun resetAll(count: Int)

    /**设置复活币数量**/
    fun setCount(player: Player, number: Int)

    /**获取复活币数量**/
    fun getCount(player: Player) : Int

}