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

        if (player.hasPermission("ResidenceFly.allow")) return;

        if (residence != null && residence.getPermissions().playerHas(player , Flags.fly ,false))
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
