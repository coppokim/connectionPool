package com.epam.kim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionPool {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPool.class);
    private String driverName;
    private String url;
    private String userName;
    private String password;
    private int connMaxCount = 4;
    private CopyOnWriteArrayList<Connection> freeConnections = new CopyOnWriteArrayList<Connection>();

    public ConnectionPool(String driverName, String url, String userName, String password, int connMaxCount, CopyOnWriteArrayList<Connection> freeConnections) {
        this.driverName = driverName;
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.connMaxCount = connMaxCount;
        for (int i = 0; i < connMaxCount; i++) {
            freeConnections.add(initConnection());
        }
    }

    public Connection initConnection(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    public synchronized Connection getConnection() throws SQLException {
        Connection newConn = null;
        if (freeConnections.size() == 0) {
            log.error("There is no available connection");
        } else {
            newConn = (Connection) freeConnections.get(freeConnections.size() - 1);
            freeConnections.remove(newConn);
        }
        return newConn;
    }
    public synchronized void addConnection(Connection connection) throws NullPointerException {
        if (connection != null) {
            freeConnections.add(connection);
        }else throw new NullPointerException("Connection not added ");

    }

}
