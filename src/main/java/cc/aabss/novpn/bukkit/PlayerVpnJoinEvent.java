package cc.aabss.novpn.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

@SuppressWarnings("unused")
public class PlayerVpnJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final InetSocketAddress ipAddress;

    public PlayerVpnJoinEvent(@Nonnull final Player player, @Nonnull final InetSocketAddress ipAddress) {
        this.player = player;
        this.ipAddress = ipAddress;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public InetSocketAddress getAddress() {
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