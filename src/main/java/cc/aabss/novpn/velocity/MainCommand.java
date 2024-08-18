package cc.aabss.novpn.velocity;

import cc.aabss.novpn.NoVpnVelocity;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class MainCommand implements SimpleCommand {

    private final NoVpnVelocity plugin;

    public MainCommand(NoVpnVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();
        if (args.length > 0 && args[0].equals("reload")) {
            plugin.config.reloadConfig();
            source.sendMessage(miniMessage().deserialize("<green>Reloaded!"));
        } else {
            source.sendMessage(miniMessage().deserialize("<red>/novpn reload"));
        }
    }

    @Override
    public boolean hasPermission(SimpleCommand.Invocation invocation) {
        return invocation.source().hasPermission("novpn.command");
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();
        if ("reload".startsWith(args[0])) return CompletableFuture.completedFuture(List.of("reload"));
        return CompletableFuture.completedFuture(List.of());
    }
}
