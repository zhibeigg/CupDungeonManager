package com.github.cupdungeonmanager.all.factory.limit

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.all.api.events.player.PlayerLimitEvents
import com.github.cupdungeonmanager.all.storage.Storage
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendLang
import java.time.LocalDateTime

object LimitManager {

    private var reset = false

    @SubscribeEvent
    fun addData(e: PlayerJoinEvent) {
        if (Storage.INSTANCE.getData(e.player).getConfigurationSection("save") == null) {
            Storage.INSTANCE.resetLimit(e.player)
        }
    }

    @Schedule(period = 20 * 20, async = true)
    fun reset() {
        val time = LocalDateTime.now()
        val hour = time.hour
        val min = time.minute
        if (hour == (config.getString("刷新时间", "0:0")?.split(":")?.get(0).toString().toInt()) && min == (config.getString("刷新时间", "0:0")?.split(":")?.get(1).toString().toInt())) {
            if (!reset) {
                reset = true
                resetALL(config.getInt("count"))
            }
        } else {
            reset = false
        }
    }


    fun resetALL(count: Int = 0) {
        PlayerLimitEvents.Reset().call()
        Bukkit.getOnlinePlayers().forEach {
            it.sendLang("limit-reset", it.displayName)
        }
        Storage.INSTANCE.resetAll(count)
    }

}