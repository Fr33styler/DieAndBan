package ro.fr33styler.dieandban.config;

import org.bukkit.configuration.ConfigurationSection;

public class Settings {

    private final int banAtDeaths;

    public Settings(ConfigurationSection section) {
        this.banAtDeaths = section.getInt("ban-after-deaths");
    }

    public int getBanAtDeaths() {
        return banAtDeaths;
    }

}
