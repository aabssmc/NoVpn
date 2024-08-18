package cc.aabss.novpn;

import cc.aabss.novpn.bungeecord.MainCommand;
import cc.aabss.novpn.bungeecord.MetricsBungee;
import cc.aabss.novpn.bungeecord.PlayerVpnJoinEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

import static org.bukkit.Bukkit.getConsoleSender;

public class NoVpnBungee extends Plugin implements Listener, NoVpn {

    public File configFile;
    public Configuration config;
    public ConfigurationProvider provider;

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
        new MetricsBungee(this, 23078);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MainCommand("novpn", "novpn.command", this));
        configFile = new File(getDataFolder(), "config.yml");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.yml");
                FileWriter writer = new FileWriter(configFile);
                writer.write(new String(inputStream.readAllBytes()));
                writer.close();
            }
            provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            config = provider.load(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        getConsoleSender().sendMessage(event.getPlayer().getName() + " " + ip);
        if (isVpn(ip)) {
            for (ProxiedPlayer p : getProxy().getPlayers()) {
                if (p.hasPermission("novpn.sendVpnMessage")) {
                    if (config.getString("message-sender").equalsIgnoreCase("PROXY") ||
                            config.getString("message-sender").equalsIgnoreCase("BOTH")) {
                        p.sendMessage(prefix() + event.getPlayer().getName() + " may be using a vpn.");
                    }
                }
            }
            getConsoleSender().sendMessage(prefix() + event.getPlayer().getName() + " may be using a vpn.");
            getProxy().getPluginManager().callEvent(new PlayerVpnJoinEvent(event.getPlayer(), event.getPlayer().getAddress()));
            for (String console : config.getStringList("commands.console")) {
                getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), console
                        .replaceAll("%player%", event.getPlayer().getName())
                        .replaceAll("%ip%", ip));
            }
            for (String player : config.getStringList("commands.player")) {
                getProxy().getPluginManager().dispatchCommand(event.getPlayer(), player
                        .replaceAll("%player%", event.getPlayer().getName())
                        .replaceAll("%ip%", ip));
            }

            if (config.getBoolean("should-kick")) {
                event.getPlayer().disconnect("You may be using a VPN.");
            }
        }
    }

    public String prefix() {
        return ChatColor.GRAY+"["+ChatColor.RED+"NOVPN"+ChatColor.GRAY+"] ";
    }
}
