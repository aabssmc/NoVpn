package cc.aabss.novpn.bungeecord;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.net.InetSocketAddress;

public class PlayerVpnJoinEvent extends Event {
    private final ProxiedPlayer player;
    private final InetSocketAddress ip;

    public PlayerVpnJoinEvent(ProxiedPlayer player, InetSocketAddress ip) {
        this.player = player;
        this.ip = ip;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public InetSocketAddress getIp() {
        return ip;
    }
}
