package me.stevenlol.butter.sql;

import me.stevenlol.butter.Main;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class MySQL {

    private final Object lock = new Object();

    private String host = Main.getPlugin().getConfig().getString("database.host");
    private int port = Main.getPlugin().getConfig().getInt("database.port");
    private String database = Main.getPlugin().getConfig().getString("database.database");
    private String username = Main.getPlugin().getConfig().getString("database.username");
    private String password = Main.getPlugin().getConfig().getString("database.password");
    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public CompletableFuture<ResultSet> query(final PreparedStatement statement) {
        CompletableFuture<ResultSet> completableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            synchronized (this.lock) {
                ResultSet resultSet = null;
                try {
                    resultSet = statement.executeQuery();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                completableFuture.complete(resultSet);
            }
        });
        return completableFuture;
    }

    public void update(final PreparedStatement statement) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            synchronized (this.lock) {
                try {
                    statement.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    public PreparedStatement createStatement(String sql) {
        try {
            return this.connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
