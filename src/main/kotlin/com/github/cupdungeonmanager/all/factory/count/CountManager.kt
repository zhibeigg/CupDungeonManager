package com.github.cupdungeonmanager.all.factory.count

import com.github.cupdungeonmanager.CupDungeonManager.config
import org.bukkit.event.entity.PlayerDeathEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.ui.type.Basic

object CountManager {

    @SubscribeEvent
    fun e(e: PlayerDeathEvent) {
        val player = e.entity
        val ui = Basic(config.getString("复活界面") ?: "炁之复活")
        ui.
    }

}