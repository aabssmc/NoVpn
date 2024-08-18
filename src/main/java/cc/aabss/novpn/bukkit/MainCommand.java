package cc.aabss.novpn.bukkit;

import cc.aabss.novpn.NoVpnBukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nonnull;
import java.util.List;

public class MainCommand implements TabExecutor {

    public MainCommand(NoVpnBukkit plugin) {
        this.plugin = plugin;
    }

    private final NoVpnBukkit plugin;

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if (args.length > 0 && args[0].equals("reload")) {
            plugin.saveConfig();
            plugin.reloadConfig();
            sender.sendMessage(plugin.prefix()+ ChatColor.GREEN+"Reloaded!");
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
}
