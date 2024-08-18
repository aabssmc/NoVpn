package cc.aabss.novpn.bungeecord;

import cc.aabss.novpn.NoVpnBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.bukkit.ChatColor;

import java.io.IOException;

public class MainCommand extends Command {
    public MainCommand(String name, String permission, NoVpnBungee plugin) {
        super(name, permission);
        this.plugin = plugin;
    }

    private final NoVpnBungee plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0 && args[0].equals("reload")) {
            try {
                plugin.provider.save(plugin.config, plugin.configFile);
                plugin.config = plugin.provider.load(plugin.configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage(plugin.prefix()+ ChatColor.GREEN+"Reloaded!");
        } else {
            sender.sendMessage(ChatColor.RED+"/novpn reload");
        }
    }
}
