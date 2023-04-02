package com.github.cupdungeonmanager.all.factory.count

import com.github.cupdungeonmanager.all.api.events.PluginReloadEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object UI {

    @Config("ui.yml")
    lateinit var config : Configuration

    @SubscribeEvent
    fun e(e : PluginReloadEvent) {
        config.reload()
    }


}