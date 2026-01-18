package ro.fr33styler.dieandban.database;

import ro.fr33styler.dieandban.DieAndBan;
import ro.fr33styler.dieandban.holder.DeathHolder;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class PlayerData {

    private final DieAndBan plugin;
    private final Connection connection;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PlayerData(DieAndBan plugin, File dataFolder) throws SQLException, ClassNotFoundException {
        this.plugin = plugin;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + new File(dataFolder, "playerdata.db"));
        createTable();
    }

    private void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS die_and_ban (uuid VARCHAR(36) UNIQUE, deaths int);");
        }
    }

    public void fetchOrInsert(UUID uuid, DeathHolder holder) {
        executor.submit(() -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT deaths FROM die_and_ban WHERE uuid = ? LIMIT 1;")) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        holder.setDeaths(result.getInt(1));
                    } else {
                        insert(uuid);
                    }
                }
            } catch (SQLException exception) {
                plugin.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
        });
    }

    private void insert(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO die_and_ban VALUES (?, 0);")) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    public void updateAccount(UUID uuid, DeathHolder holder) {
        executor.submit(() -> {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE die_and_ban SET deaths = ? WHERE uuid = ?;")) {
                statement.setInt(1, holder.getDeaths());
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException exception) {
                plugin.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
        });
    }

    public void close() {
        executor.shutdownNow().forEach(Runnable::run);
        try {
            connection.close();
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

}
