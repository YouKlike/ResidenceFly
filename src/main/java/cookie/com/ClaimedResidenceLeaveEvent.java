package cookie.com;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ClaimedResidenceLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private @NotNull
    final Player player;

    public ClaimedResidenceLeaveEvent(final @NotNull Player player) {
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
