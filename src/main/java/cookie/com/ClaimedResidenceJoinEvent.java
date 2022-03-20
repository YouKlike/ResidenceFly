package cookie.com;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClaimedResidenceJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private @NotNull
    final Player player;

    private @NotNull
    final ClaimedResidence residence;

    public ClaimedResidenceJoinEvent(final @NotNull Player player, ClaimedResidence residence) {
        this.player = player;
        this.residence = residence;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public ClaimedResidence getResidence() {
        return residence;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
