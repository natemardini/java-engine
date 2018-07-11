package framework.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    private static String defaultUrl = "jdbc:postgresql://localhost:5432/cyehia";

    private Connection connection;
    private DSLContext context;

    public Database() {
        try {
            connection = DriverManager.getConnection(defaultUrl, "cyehia", "");
            context = DSL.using(connection, SQLDialect.POSTGRES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(defaultUrl, "cyehia", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    private void setContext() {
        Connection conn = getConnection();
        context = DSL.using(conn, SQLDialect.POSTGRES);
    }

    public DSLContext getContext() {
        try {
            if (context == null || connection == null || connection.isClosed()) {
                setContext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return context;
    }
}
