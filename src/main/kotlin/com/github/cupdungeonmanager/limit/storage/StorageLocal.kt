package com.github.cupdungeonmanager.limit.storage

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.CupDungeonManager.debug
import com.github.cupdungeonmanager.limit.util.files
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Configuration

class StorageLocal : Storage {

    override fun clearLimit(player: Player, dungeon: String) {
        setLimit(player, 0, dungeon)
    }

    override fun resetLimit(player: Player) {
        config.getConfigurationSection("limit")?.toMap()?.forEach {
            setLimit(player, it.value.toString().toInt(), it.key)
            debug("地牢：${it.key}, 次数：${Storage.INSTANCE.getLimit(player, it.key)}")
        }
    }

    override fun getLimit(player: Player, dungeon: String) : Int {
        return if (getData(player)["limit.$dungeon"] == null) {
            0
        } else {
            getData(player)["limit.$dungeon"].toString().toInt()
        }
    }

    override fun setLimit(player: Player, number: Int, dungeon: String) {
        val data = getData(player)
        data["limit.$dungeon"] = number
        data.saveToFile(newFile(getDataFolder(), "/limit/${player.uniqueId}.yml"))
    }

    fun setLimit(number: Int, dungeon: String, data: Configuration) {
        data["limit.$dungeon"] = number
    }

    override fun resetall() {
        files("limit") {
            config.getConfigurationSection("limit")?.toMap()?.forEach { map ->
                setLimit(map.value.toString().toInt(), map.key, Configuration.loadFromFile(it))
            }
        }
    }

    fun getData(player: Player) : Configuration {
        return Configuration.loadFromFile(newFile(getDataFolder(), "/limit/${player.uniqueId}.yml"))
    }

}