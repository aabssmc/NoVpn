package cc.aabss.novpn.velocity;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public class ConfigManager {

    private final File configFile;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode rootNode;

    public ConfigManager(Path configDirectory) {
        this.configFile = configDirectory.resolve("config.yml").toFile();
        this.loader = YamlConfigurationLoader.builder().file(configFile).build();
        loadConfig();
    }

    public void loadConfig() {
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                    if (in != null) {
                        try (FileWriter fileWriter = new FileWriter(configFile)) {
                            fileWriter.write(new String(in.readAllBytes()));
                        }
                    } else {
                        throw new RuntimeException("Default config.yml not found in resources.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            rootNode = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public List<String> getConsoleCommands() {
        try {
            return rootNode.node("commands", "console").getList(String.class, List.of("kick %player% You are using a VPN!"));
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getPlayerCommands() {
        try {
            return rootNode.node("commands", "player").getList(String.class, List.of());
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean shouldKick() {
        return rootNode.node("should-kick").getBoolean(true);
    }

    public String getMessageSender() {
        return rootNode.node("message-sender").getString("BOTH");
    }
}
