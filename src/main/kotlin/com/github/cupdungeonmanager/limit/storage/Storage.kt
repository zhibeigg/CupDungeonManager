package com.github.cupdungeonmanager.limit.storage

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

    fun resetLimit(player: Player)

    fun clearLimit(player: Player, dungeon: String)

    fun setLimit(player: Player, number: Int, dungeon: String)

    fun getLimit(player: Player, dungeon: String) : Int

    fun resetall()

}