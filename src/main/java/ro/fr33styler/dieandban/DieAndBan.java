package ro.fr33styler.dieandban;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import ro.fr33styler.dieandban.config.Messages;
import ro.fr33styler.dieandban.config.Settings;
import ro.fr33styler.dieandban.database.PlayerData;
import ro.fr33styler.dieandban.events.Events;
import ro.fr33styler.dieandban.holder.DeathHolder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DieAndBan extends JavaPlugin {

    private Settings settings;
    private Messages messages;
    private PlayerData playerData;
    private final Events events = new Events(this);
    private final Map<UUID, DeathHolder> deaths = new HashMap<>();

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        ConfigurationSection settingsSection = config.getConfigurationSection("settings");
        if (settingsSection != null) {
            settings = new Settings(settingsSection);
        }

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            messages = new Messages(messagesSection);
        }

        try {
            playerData = new PlayerData(this, getDataFolder());
        } catch (SQLException | ClassNotFoundException exception) {
            getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            setEnabled(false);
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            DeathHolder holder = new DeathHolder();
            deaths.put(uuid, holder);

            playerData.fetchOrInsert(uuid, holder);
        }

        getServer().getPluginManager().registerEvents(events, this);
    }

    @Override
    public void onDisable() {
        deaths.clear();
        if (playerData != null) {
            playerData.close();
        }
        HandlerList.unregisterAll(events);
    }

    public Settings getSettings() {
        return settings;
    }

    public Messages getMessages() {
        return messages;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Map<UUID, DeathHolder> getDeaths() {
        return deaths;
    }

}
