package me.stevenlol.butter.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private Connection connection;
    private String host, database, username, password;
    private int port;

    public MySQL() {
        host = "localhost";
        port = 3306;
        database = "main";
        username = "root";
        password = "";
    }

    public void connect() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    public void disconnect() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean connected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public PreparedStatement createStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

}
