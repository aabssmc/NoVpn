package cc.aabss.novpn;

import cc.aabss.novpn.bukkit.MainCommand;
import cc.aabss.novpn.bukkit.MetricsBukkit;
import cc.aabss.novpn.bukkit.PlayerVpnJoinEvent;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.util.Objects;

import static org.bukkit.Bukkit.getConsoleSender;

public final class NoVpnBukkit extends JavaPlugin implements Listener, NoVpn {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        MainCommand command = new MainCommand(this);
        Objects.requireNonNull(getCommand("novpn")).setTabCompleter(command);
        Objects.requireNonNull(getCommand("novpn")).setExecutor(command);
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::updateList, 0, 12*60*60*20 /*12 hours*/);
        new MetricsBukkit(this, 23076);
    }

    @EventHandler
    @SuppressWarnings("all")
    public void onJoin(PlayerJoinEvent event) {
        InetAddress ip = event.getPlayer().getAddress().getAddress();
        getConsoleSender().sendMessage(event.getPlayer().getName() + " " + ip.getHostAddress());
        if (isVpn(ip.getHostAddress())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("novpn.sendVpnMessage")) {
                    if (getConfig().getString("message-sender").equalsIgnoreCase("BUKKIT") ||
                            getConfig().getString("message-sender").equalsIgnoreCase("BOTH")) {
                        p.sendMessage(prefix() + event.getPlayer().getName() + " may be using a vpn.");
                    }
                }
            }
            getConsoleSender().sendMessage(prefix() + event.getPlayer().getName() + " may be using a vpn.");
            getServer().getPluginManager().callEvent(new PlayerVpnJoinEvent(event.getPlayer(), event.getPlayer().getAddress()));
            try {
                for (String console : getConfig().getStringList("commands.console")) {
                    getServer().dispatchCommand(getConsoleSender(), console
                            .replaceAll("%player%", event.getPlayer().getName())
                            .replaceAll("%ip%", ip.getHostAddress()));
                }
                for (String player : getConfig().getStringList("commands.player")) {
                    getServer().dispatchCommand(event.getPlayer(), player
                            .replaceAll("%player%", event.getPlayer().getName())
                            .replaceAll("%ip%", ip.getHostAddress()));
                }
            } catch (CommandException ignored) {}

            if (getConfig().getBoolean("should-kick")) {
                event.getPlayer().kickPlayer("You may be using a VPN.");
            }
        }
    }

    public String prefix() {
        return ChatColor.GRAY+"["+ChatColor.RED+"NOVPN"+ChatColor.GRAY+"] ";
    }
}
