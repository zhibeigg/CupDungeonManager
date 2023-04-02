package com.github.cupdungeonmanager.all.storage

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.CupDungeonManager.debug
import com.github.cupdungeonmanager.all.util.files
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
        data.saveToFile(newFile(getDataFolder(), "/save/${player.uniqueId}.yml"))
    }

    fun setLimit(number: Int, dungeon: String, data: Configuration, name: String) {
        data["limit.$dungeon"] = number
        data.saveToFile(newFile(getDataFolder(), "/save/${name}"))
    }

    override fun resetAll(count: Int) {
        files("save") {
            val configuration = Configuration.loadFromFile(it)
            config.getConfigurationSection("limit")?.toMap()?.forEach { map ->
                setLimit(map.value.toString().toInt(), map.key, configuration, it.name)
            }
            setCount(count + configuration.getInt("count"), configuration, it.name)
        }
    }

    override fun getCount(player: Player) : Int {
        return if (getData(player)["count"] == null) {
            0
        } else {
            getData(player)["count"].toString().toInt()
        }
    }

    override fun setCount(player: Player, number: Int) {
        val data = getData(player)
        data["count"] = number
        data.saveToFile(newFile(getDataFolder(), "/save/${player.uniqueId}.yml"))
    }

    fun setCount(number: Int, data: Configuration, name: String) {
        data["count"] = number
        data.saveToFile(newFile(getDataFolder(), "/save/${name}"))
    }

    fun getData(player: Player) : Configuration {
        return Configuration.loadFromFile(newFile(getDataFolder(), "/save/${player.uniqueId}.yml"))
    }

}