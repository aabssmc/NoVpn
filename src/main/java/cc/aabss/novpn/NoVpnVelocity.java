package cc.aabss.novpn;

import cc.aabss.novpn.velocity.ConfigManager;
import cc.aabss.novpn.velocity.MainCommand;
import cc.aabss.novpn.velocity.MetricsVelocity;
import cc.aabss.novpn.velocity.PlayerVpnJoinEvent;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class NoVpnVelocity implements NoVpn {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final MetricsVelocity.Factory metricsFactory;
    public final ConfigManager config;

    @Inject
    public NoVpnVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, MetricsVelocity.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
        this.config = new ConfigManager(dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        metricsFactory.make(this, 23077);
        CommandManager commandManager = server.getCommandManager();
        commandManager.register("novpn", new MainCommand(this));
    }

    public void onJoin(ServerConnectedEvent event) {
        if (isVpn(event.getPlayer().getRemoteAddress().getAddress().getHostAddress())) {
            for (Player p : server.getAllPlayers()) {
                if (p.hasPermission("novpn.sendVpnMessage")) {
                    if (config.getMessageSender().equalsIgnoreCase("PROXY") ||
                            config.getMessageSender().equalsIgnoreCase("BOTH")) {
                        p.sendMessage(prefix().append(mini("<gray>"+event.getPlayer().getUsername()+" may be using a VPN.")));
                    }
                }
            }
            server.getConsoleCommandSource().sendMessage(prefix().append(mini("<gray>"+event.getPlayer().getUsername()+" may be using a VPN.")));
            server.getEventManager().fire(new PlayerVpnJoinEvent(event.getPlayer(), event.getPlayer().getRemoteAddress()));
            server.getEventManager().register(this, this);
            for (String console : config.getConsoleCommands()) {
                server.getCommandManager().executeAsync(server.getConsoleCommandSource(), console
                        .replaceAll("%player%", event.getPlayer().getUsername())
                        .replaceAll("%ip%", event.getPlayer().getRemoteAddress().getAddress().getHostAddress()));
            }
            for (String player : config.getPlayerCommands()) {
                server.getCommandManager().executeAsync(event.getPlayer(), player
                        .replaceAll("%player%", event.getPlayer().getUsername())
                        .replaceAll("%ip%", event.getPlayer().getRemoteAddress().getAddress().getHostAddress()));
            }

            if (config.shouldKick()) {
                event.getPlayer().disconnect(Component.text("You may be using a VPN."));
            }
        }
    }

    public Component prefix() {
        return mini("<gray>[<red>NOVPN</red>] ");
    }

    public Component mini(String input) {
        return miniMessage().deserialize(input);
    }
}
