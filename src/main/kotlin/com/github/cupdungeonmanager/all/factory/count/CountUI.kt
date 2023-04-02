package com.github.cupdungeonmanager.all.factory.count

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.serverct.ersha.dungeon.DungeonPlus
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import taboolib.platform.util.sendLang

class CountUI(val viewer: Player) {

    companion object {

        val root: ConfigurationSection
            get() = UI.config.getConfigurationSection("revive")!!

        val title: String
            get() = root.getString("title")!!

        val rows: Int
            get() = root.getInt("rows", 3)

        val revive: List<Int>
            get() = root.getIntegerList("revive.slots")

        val giveUP: List<Int>
            get() = root.getIntegerList("giveUP.slots")

        val team: List<Int>
            get() = root.getIntegerList("team.slots")

        val info: List<Int>
            get() = root.getIntegerList("info.slots")

        val reviveIcon: ItemStack
            get() = root.getItemStack("revive")!!

        val giveUPIcon: ItemStack
            get() = root.getItemStack("giveUP")!!

        val infoIcon: ItemStack
            get() = root.getItemStack("info")!!

        val teamIcon: ItemStack
            get() = root.getItemStack("team")!!

    }

    fun open() {
        viewer.openMenu<Basic>(title) {
            rows(rows)
            root.getKeys(false).filter { it.startsWith("icon-") }.forEach {
                val itemStack = buildItem(root.getItemStack(it)!!) {
                    flags += ItemFlag.values()
                }
                root.getIntegerList("${it}.slots").forEach { slot ->
                    set(slot, itemStack)
                }
            }
            val reviveItem = getPapiLore(reviveIcon)
            revive.forEach {
                set(it, reviveItem)
            }
            val giveUPItem = getPapiLore(giveUPIcon)
            giveUP.forEach {
                set(it, giveUPItem)
            }
            val infoItem = getPapiLore(infoIcon)
            info.forEach {
                set(it, infoItem)
            }
            val teamItem = getPapiLore(teamIcon)
            info.forEach {
                set(it, teamItem)
            }
            onClick { event ->
                event.isCancelled = true
                val factory = PlayerCount(viewer)
                if (revive.contains(event.rawSlot)) {
                    if (factory.get() > 0) {
                        factory.reduce(1)
                        viewer.gameMode = GameMode.SURVIVAL
                        viewer.sendLang("use-count", viewer.displayName)
                        viewer.closeInventory()
                    } else {
                        viewer.sendLang("use-count-not-enough", viewer.displayName, factory.get())
                    }
                }
                if (giveUP.contains(event.rawSlot)) {
                    if (DungeonPlus.dungeonManager.isDungeonWorld(viewer.world)) {
                        if (check()) {
                            viewer.closeInventory()
                            val viewered = getTeamPlayer()[0]
                            viewer.sendLang("look-team", viewer.displayName, viewered.displayName)
                            viewer.spectatorTarget = viewered
                        } else {
                            viewer.closeInventory()
                            DungeonPlus.dungeonManager.getDungeon(viewer.world)
                                ?.let { DungeonPlus.dungeonManager.removeDungeon(it) }
                        }
                    }
                }
                if (info.contains(event.rawSlot)) {
                    viewer.closeInventory()
                }
            }

        }
    }

    fun check(): Boolean {
        DungeonPlus.teamManager.getTeam(viewer)?.team?.players?.forEach {
            val player = Bukkit.getPlayer(it)
            if (player?.isDead == false) {
                return true
            }
        }
        return false
    }

    fun getTeamPlayer(): MutableList<Player> {
        val players = mutableListOf<Player>()
        DungeonPlus.teamManager.getTeam(viewer)?.team?.players?.forEach {
            players.add(Bukkit.getPlayer(it)!!)
        }
        return players
    }

    fun getPapiLore(item: ItemStack): ItemStack {
        return buildItem(item) {
            name = name?.let { PlaceholderAPI.setPlaceholders(viewer, it) }
            val papi = PlaceholderAPI.setPlaceholders(viewer, lore)
            lore.clear()
            lore.addAll(papi)
        }
    }

}