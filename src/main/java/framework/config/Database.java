package framework.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class Database {
    private static String defaultUrl = "jdbc:postgresql://localhost:5432/cyehia";

    private static Connection connection;
    private static DSLContext context;

    private static void connect() {
        try {
            connection = DriverManager.getConnection(defaultUrl, "cyehia", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    private static void setContext() {
        Connection conn = getConnection();
        context = DSL.using(conn, SQLDialect.POSTGRES);
    }

    public static DSLContext getContext() {
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
