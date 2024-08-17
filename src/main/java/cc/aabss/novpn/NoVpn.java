package cc.aabss.novpn;

import cc.aabss.novpn.vpn.Interval;
import cc.aabss.novpn.vpn.IntervalTree;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static cc.aabss.novpn.vpn.Checker.*;
import static org.bukkit.Bukkit.getConsoleSender;

public final class NoVpn extends JavaPlugin implements Listener {

    private static IntervalTree itree = new IntervalTree();
    private static final String prefix = ChatColor.GRAY +"["+ChatColor.RED+"NOVPN"+ChatColor.GRAY+"] ";

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if (args[0].equals("reload")) {
            saveConfig();
            reloadConfig();
            sender.sendMessage(prefix+ChatColor.GREEN+"Reloaded!");
        } else {
            sender.sendMessage(ChatColor.RED+"/novpn reload");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, String[] args) {
        if ("reload".startsWith(args[0])) return List.of("reload");
        return List.of();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Objects.requireNonNull(getCommand("novpn")).setTabCompleter(this);
        Objects.requireNonNull(getCommand("novpn")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, NoVpn::updateList, 0, 12*60*60*20 /*12 hours*/);
    }

    @EventHandler
    @SuppressWarnings("all")
    public void onJoin(PlayerJoinEvent event) {
        InetAddress ip = event.getPlayer().getAddress().getAddress();
        getConsoleSender().sendMessage(event.getPlayer().getName() + " " + ip.getHostAddress());
        if (isVpn(ip.getHostAddress())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("novpn.sendVpnMessage")) {
                    p.sendMessage(prefix + event.getPlayer().getName() + " may be using a vpn.");
                }
            }
            getConsoleSender().sendMessage(prefix + event.getPlayer().getName() + " may be using a vpn.");
            getServer().getPluginManager().callEvent(new PlayerVpnJoinEvent(event.getPlayer(), ip));
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
        }
    }

    public static boolean isVpn(String ip) {
        int ipBinary = ipToBinary(ip);
        return itree.query(ipBinary);
    }

    public static void updateList() {
        CompletableFuture.runAsync(() -> {
            try {
                IntervalTree newTree = new IntervalTree();
                String ipv4CidrRanges = fetchCidrRanges();
                if (ipv4CidrRanges != null) {
                    String[] ranges = ipv4CidrRanges.split("\n");
                    for (String cidr : ranges) {
                        Interval range = ipv4CidrToRange(cidr);
                        newTree.insert(range);
                    }
                }
                itree = newTree;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).whenCompleteAsync((unused, throwable) -> getConsoleSender().sendMessage(prefix +"VPN ips updated."));
    }
}
