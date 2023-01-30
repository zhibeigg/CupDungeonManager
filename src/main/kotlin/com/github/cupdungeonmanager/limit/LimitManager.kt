package com.github.cupdungeonmanager.limit

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.limit.storage.Storage
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendLang
import java.time.LocalDateTime

object LimitManager {

    @SubscribeEvent
    fun addData(e: PlayerJoinEvent) {
        if (Storage.INSTANCE.getData(e.player).getConfigurationSection("limit") == null) {
            Storage.INSTANCE.resetLimit(e.player)
        }
    }

    @Schedule(true, 0, 20*50)
    fun reset() {
        val time = LocalDateTime.now()
        if (time.hour == (config.getString("刷新时间", "00:00")?.split(":")?.get(0).toString().toInt())) {
            if (time.minute == (config.getString("刷新时间", "00:00")?.split(":")?.get(1).toString().toInt())) {
                resetALL()
                Thread.sleep(1200)
            }
        }
    }

    fun resetALL() {
        Bukkit.getOnlinePlayers().forEach {
            it.sendLang("limit-reset", it.displayName)
        }
        Storage.INSTANCE.resetall()
    }

}