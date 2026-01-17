package ro.fr33styler.dieandban.events;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import ro.fr33styler.dieandban.DieAndBan;
import ro.fr33styler.dieandban.holder.DeathHolder;

import java.util.UUID;

public class Events implements Listener {

    private final DieAndBan plugin;

    public Events(DieAndBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();
        DeathHolder holder = new DeathHolder();
        plugin.getDeaths().put(uuid, holder);

        plugin.getPlayerData().synchroniseOrInsert(uuid, holder);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        Player player = event.getEntity();

        UUID uuid = player.getUniqueId();
        DeathHolder holder = plugin.getDeaths().get(uuid);
        if (holder == null) return;

        holder.setDeaths(holder.getDeaths() + 1);
        plugin.getPlayerData().updateAccount(uuid, holder);

        if (holder.getDeaths() < plugin.getSettings().getBanAtDeaths()) return;

        String banMessage = plugin.getMessages().getBanMessage();
        player.kickPlayer(banMessage);
        plugin.getServer().getBanList(BanList.Type.NAME).addBan(player.getName(), banMessage, null, null);

        Bukkit.broadcastMessage(plugin.getMessages().getBroadcastLostLives().replace("%player%", player.getName()));
    }

}
