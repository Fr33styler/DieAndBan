package ro.fr33styler.dieandban.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class Messages {

    private final String banMessage;
    private final String broadcastLostLives;

    public  Messages(ConfigurationSection section) {
        banMessage = replace(section.getString("ban-message"));
        broadcastLostLives = replace(section.getString("broadcast-lost-lives"));
    }

    private String replace(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String getBanMessage() {
        return banMessage;
    }

    public String getBroadcastLostLives() {
        return broadcastLostLives;
    }
}
