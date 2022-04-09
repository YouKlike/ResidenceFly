/*-
 * LICENSE
 * ResidenceFly
 * -------------
 * Copyright (C) 2022 YouKlike
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package cookie.com;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ResidenceFly extends JavaPlugin implements Listener {

    private YamlConfiguration configuration;

    @Override
    public void onEnable() {

        this.configLoad();
        getServer().getPluginManager().registerEvents(this , this);
    }

    // 加載配置config.yml
    public void configLoad() {
        File f = new File(getDataFolder() + "/config.yml");
        if (f.exists()) {
            getServer().getConsoleSender().sendMessage("[領地飛行] : 檢測到config.yml,開始加載配置");
        } else {
            getServer().getConsoleSender().sendMessage("[領地飛行] : 未檢測到config.yml,開始創建配置");
            this.saveResource("config.yml" , false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(f);
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        final ClaimedResidence residence =
                Residence.getInstance().getResidenceManager().getByLoc(player);

        if (player.hasPermission("ResidenceFly.bypass")) return;

        if (residence != null && residence.getPermissions().playerHas(player , Flags.fly ,false) && player.hasPermission("ResidenceFly.allow"))
            Bukkit.getPluginManager().callEvent(new ClaimedResidenceJoinEvent(player, residence));

        else
            Bukkit.getPluginManager().callEvent(new ClaimedResidenceLeaveEvent(player));
    }

    public void tpPlayerToSafeLocation(Player player) {
        // 獲取玩家當前位置
        final Location location = player.getLocation();

        // 迴圈判斷有效位置
        for (int y = location.getBlockY(); y >= 0; y--) {
            // 獲取所在位置的方塊
            final Block blockAt =
                    player.getWorld().getBlockAt
                            (new Location(player.getWorld(), location.getBlockX(), y , location.getBlockZ()));
            // 如果方塊不是空氣、可通過，代表是安全位置
            if (!blockAt.getType().equals(Material.AIR) && !blockAt.isPassable()) {
                // 將玩家傳送至此
                player.teleport(new Location(player.getWorld(), location.getBlockX(), y + 2.5D , location.getBlockZ(), location.getYaw(), location.getPitch()));
                return; // 返回
            }
        }
    }

    @EventHandler
    public void join(@NotNull ClaimedResidenceJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.getAllowFlight()) {
            player.spawnParticle(Particle.REDSTONE, player.getLocation(), 5, 0.2f, 0.2f, 0.2f, new Particle.DustOptions(Color.PURPLE, 2));
            @Nullable String joinTitle = this.configuration.getString("JoinTitle");
            player.sendMessage(joinTitle != null ? joinTitle : "");
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void leave(@NotNull ClaimedResidenceLeaveEvent event) {
        final Player player = event.getPlayer();

        if (player.getAllowFlight()) {
            player.spawnParticle(Particle.REDSTONE, player.getLocation(), 5, 0.2f, 0.2f, 0.2f, new Particle.DustOptions(Color.GREEN, 2));
            @Nullable String leaveTitle = this.configuration.getString("LeaveTitle");
            player.sendMessage(leaveTitle != null ? leaveTitle : "");
            this.tpPlayerToSafeLocation(player);
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

}
