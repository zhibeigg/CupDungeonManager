package com.github.cupdungeonmanager.all.api.manager

import com.github.cupdungeonmanager.all.factory.count.PlayerCount
import org.bukkit.entity.Player

object CountManager {

    /**
     * 增加此玩家的复活币数值，返回玩家剩余复活币数量
     *
     * @param Player 玩家
     * @param count 复活币
     */
    fun add(player: Player, count: Int) : Int {
        val playerCount = PlayerCount(player)
        playerCount.add(count)
        return playerCount.get()
    }

    /**
     * 减少此玩家的复活币数值，返回玩家剩余复活币数量
     *
     * @param Player 玩家
     * @param count 复活币
     */
    fun reduce(player: Player, count: Int) : Int {
        val playerCount = PlayerCount(player)
        playerCount.reduce(count)
        return playerCount.get()
    }

    /**
     * 设置此玩家的复活币数值，返回玩家剩余复活币数量
     *
     * @param Player 玩家
     * @param count 复活币
     */
    fun set(player: Player, count: Int) : Int {
        val playerCount = PlayerCount(player)
        playerCount.set(count)
        return playerCount.get()
    }

    /**
     * 获取此玩家的复活币数值
     *
     * @param Player 玩家
     */
    fun get(player: Player) : Int {
        return PlayerCount(player).get()
    }

}