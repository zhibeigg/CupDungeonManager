package com.github.cupdungeonmanager

import com.germ.germplugin.api.GermKeyAPI
import com.germ.germplugin.api.KeyType
import com.github.cupdungeonmanager.all.script.DungeonSetLimitScript
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object CupDungeonManager : Plugin() {

    //加载配置文件
    @Config("config.yml", migrate = true, autoReload = true)
    lateinit var config: Configuration

    //插件启动时
    override fun onEnable() {
        if (!enable("DungeonPlus")) disablePlugin()
        enable("PlaceholderAPI")
        enable("GermPlugin")
        say("&6CupDungeonManager!&a启动成功！&cby.zhi_bei")
        DungeonSetLimitScript().register(true)
        GermKeyAPI.registerKey(KeyType.KEY_SPACE)
        say("&6萌芽按键SPACE注册成功")
        GermKeyAPI.registerKey(KeyType.KEY_LEFT)
        say("&6萌芽按键左键注册成功")
        GermKeyAPI.registerKey(KeyType.KEY_RIGHT)
        say("&6萌芽按键右键注册成功")
    }

    //插件关闭时
    override fun onDisable() {
        say("&6CupDungeonManager!&a卸载成功！&cby.zhi_bei")
    }

    //替换颜色代码
    fun parse(s: String): String {
        return s.replace("&", "§").replace("§§", "&")
    }

    //后台信息发送
    fun say(s: String?) {
        val sender: CommandSender = Bukkit.getConsoleSender()
        sender.sendMessage(s?.let { parse(it) })
    }

    fun debug(s: String) {
        if (!config.getBoolean("debug")) return
        say(s)
    }

    //检测附属插件加载
    fun enable(s: String) : Boolean {
        return if (Bukkit.getServer().pluginManager.getPlugin(s)?.isEnabled == true) {
            say("&a已开启附属 &e${s}")
            true
        } else {
            say("&c未查找到附属 &e${s}")
            false
        }
    }
}