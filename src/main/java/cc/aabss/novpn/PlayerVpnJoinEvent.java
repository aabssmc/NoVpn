package cc.aabss.novpn;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.net.InetAddress;

@SuppressWarnings("unused")
public class PlayerVpnJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final InetAddress ipAddress;

    public PlayerVpnJoinEvent(@Nonnull final Player player, @Nonnull final InetAddress ipAddress) {
        this.player = player;
        this.ipAddress = ipAddress;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public InetAddress getAddress() {
        return ipAddress;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}