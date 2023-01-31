package com.github.cupdungeonmanager.limit

import com.github.cupdungeonmanager.limit.storage.Storage
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.serverct.ersha.dungeon.common.api.annotation.AutoRegister
import org.serverct.ersha.dungeon.common.api.component.script.ActionScriptDescription
import org.serverct.ersha.dungeon.common.api.component.script.BasicActionScript
import org.serverct.ersha.dungeon.common.api.component.script.DungeonActionScript
import org.serverct.ersha.dungeon.common.api.component.script.type.ScriptType
import org.serverct.ersha.dungeon.internal.dungeon.Dungeon
import taboolib.module.chat.colored

@AutoRegister
class DungeonSetLimitScript : BasicActionScript(false) {

    override val type: Array<ScriptType> = arrayOf(ScriptType.SYSTEM)
    override val key: String = "limit-condition"
    override val mandatorySync: Boolean = false


    override val description: ActionScriptDescription = ActionScriptDescription("进入地牢次数限制")
        .type(*type)
        .sample("\$limit-condition{value=1;message=&4| &8系统 &7-> &e%player_name% &c的进入次数不足} @system")
        .append("value", "扣除的次数", true)
        .append("message", "未满足后发送的信息", true)


    var value = 1
    var message = "&4| &8系统 &7-> &e%player_name% &c的进入次数不足"


    override fun init(dungeon: Dungeon, parameter: Map<String, String>): DungeonActionScript {
        value = parameter["value"]!!.toInt()
        message = parameter["message"].toString()
        return this
    }

    override fun conditionScript(dungeon: Dungeon, scriptType: ScriptType): Boolean {
        var var1 = false
        dungeon.team.players.forEach {
            val player = Bukkit.getPlayer(it)
            if (player!=null) {
                val now = Storage.INSTANCE.getLimit(player, dungeon.dungeonName)
                if (now >= value) {
                    var1 = true
                } else {
                    dungeon.team.players.forEach {uuid ->
                        Bukkit.getPlayer(uuid)?.sendMessage(PlaceholderAPI.setPlaceholders(player, message).colored())
                    }
                }
            }
        }
        if (var1) {
            dungeon.team.players.forEach {
                val player = Bukkit.getPlayer(it)
                if (player!=null) {
                    PlayerDailyLimit(player).reduce(value, dungeon.dungeonName)
                }
            }
        }
        return var1
    }





}