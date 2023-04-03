package com.github.cupdungeonmanager.all.factory.count

import com.github.cupdungeonmanager.CupDungeonManager.config
import com.github.cupdungeonmanager.CupDungeonManager.debug
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.serverct.ersha.dungeon.DungeonPlus
import org.serverct.ersha.dungeon.internal.dungeon.Dungeon
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.chat.colored
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import taboolib.platform.util.getMeta
import taboolib.platform.util.sendLang
import taboolib.platform.util.setMeta

class CountUI(private val viewer: Player) {

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

        val noTeamIcon: ItemStack
            get() = root.getItemStack("no-team")!!


    }

    private var mubei: Entity? = null

    private val dungeon: Dungeon
        get() = DungeonPlus.dungeonManager.getDungeon(viewer)!!

    private var freeRevive = CountManager.DungeonsReviveFreeTimes[dungeon.dungeonName] ?: 0
    private var noFreeRevive = CountManager.DungeonsReviveLimit[dungeon.dungeonName] ?: 0

    fun build() : Inventory {
        return buildMenu<Basic>(title.colored()) {
            rows(rows)
            handLocked(true)
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
            team.forEachIndexed { index, i ->
                set(i, getTeam(teamIcon)[index])
            }
            onClick { event ->
                event.isCancelled = true
                val factory = PlayerCount(viewer)
                if (revive.contains(event.rawSlot)) {
                    if (freeRevive > 0) {
                        DungeonPlus.dungeonManager.getDungeon(viewer.world)?.revive(viewer,
                            defaultLocation = true,
                            force = true
                        )
                        freeRevive -= 1
                        mubei?.remove()
                        viewer.sendLang("revive", viewer.displayName, freeRevive)
                        getTeamPlayer().forEach {
                            it.sendLang("revive", viewer.displayName, freeRevive)
                        }
                        viewer.closeInventory()
                    } else if (factory.get() > 0) {
                        if (noFreeRevive > 0) {
                            factory.reduce(1)
                            noFreeRevive -= 1
                            DungeonPlus.dungeonManager.getDungeon(viewer.world)?.revive(viewer,
                                defaultLocation = true,
                                force = true
                            )
                            mubei?.remove()
                            viewer.sendLang("use-count", viewer.displayName)
                            getTeamPlayer().forEach {
                                it.sendLang("use-count", viewer.displayName)
                            }
                            viewer.closeInventory()
                        } else {
                            viewer.sendLang("revive-not-enough")
                        }
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
                if (team.contains(event.rawSlot)) {
                    val name = event.currentItem?.getItemTag()?.get("team")?.asString()
                    if (name != null) {
                        val player = Bukkit.getPlayerExact(name)!!
                        if (factory.get() > 1) {
                            if (noFreeRevive > 0) {
                                noFreeRevive -= 1
                                factory.reduce(2)
                                DungeonPlus.dungeonManager.getDungeon(viewer.world)?.revive(player,
                                    defaultLocation = true,
                                    force = true
                                )
                                player.getNearbyEntities(1000.0,1000.0,1000.0).forEach {
                                    if (it.getMeta("team").toString() == player.name) {
                                        player.teleport(it.location)
                                        it.remove()
                                    }
                                }
                                viewer.sendLang("use-count-team", viewer.displayName, player.displayName)
                                getTeamPlayer().forEach {
                                    it.sendLang("use-count-team", viewer.displayName, player.displayName)
                                }
                                player.closeInventory()
                            } else {
                                viewer.sendLang("revive-not-enough")
                            }
                        } else {
                            viewer.sendLang("use-count-not-enough", viewer.displayName, factory.get())
                        }
                    } else {
                        viewer.sendLang("team-not", viewer.displayName)
                    }
                }
            }
        }
    }

    fun open() {
        debug("open")
        viewer.openInventory(build())
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
            if (Bukkit.getPlayer(it)!! != viewer) {
                players.add(Bukkit.getPlayer(it)!!)
            }
        }
        debug(players.toString())
        return players
    }

    fun getPapiLore(item: ItemStack): ItemStack {
        return buildItem(item) {
            name = name?.let { PlaceholderAPI.setPlaceholders(viewer, it) }
            val papi = PlaceholderAPI.setPlaceholders(viewer, lore)
            lore.clear()
            lore.addAll(papi)
            lore.forEachIndexed { index, s ->
                lore[index] = s.replace("{revive}", noFreeRevive.toString()).replace("{freeRevive}", freeRevive.toString())
            }
            colored()
        }
    }

    fun getTeam(item: ItemStack): MutableList<ItemStack> {
        val players = getTeamPlayer()
        val items = mutableListOf<ItemStack>()
        players.forEach { player ->
            items.add(buildItem(item) {
                name = name?.replace("{name}", player.displayName)
                val papi = PlaceholderAPI.setPlaceholders(player, lore)
                lore.clear()
                lore.addAll(papi)
                colored()
            }.setItemTag(ItemTag().put("team", player.name).asCompound())
            )
        }
        while (items.size < 5) {
            items.add(buildItem(noTeamIcon) {
                val papi = PlaceholderAPI.setPlaceholders(viewer, lore)
                lore.clear()
                lore.addAll(papi)
                colored()
            }
            )
        }
        return items
    }

    fun mubei() {
        val entity: ArmorStand = viewer.world.spawnEntity(viewer.location.add(0.0, 0.0, 0.0), EntityType.ARMOR_STAND) as ArmorStand
        entity.isGlowing = true
        entity.isCustomNameVisible = true
        entity.setGravity(false)
        entity.customName = PlaceholderAPI.setPlaceholders(viewer, config.getString("name") ?: "%player_name% 的亡魂").colored()
        entity.setMeta("team", viewer.name)
        entity.isInvulnerable = true
    }

}