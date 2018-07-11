package framework;

import engine.base.BoaEngine;
import engine.base.BoaExchange;
import engine.base.BoaMiddleware;
import framework.entities.Firm;
import framework.entities.jooq.tables.records.FirmsRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.List;

import static framework.entities.jooq.Tables.*;


public class Application extends BoaEngine {

    public static void main(String[] args) {
        BoaEngine engine = new Application();

        engine.setPort(8001);
        engine.setMultiThreaded(true);
        engine.use(Application::logger);

        engine.start();
    }

    @Override
    public void router() {

    }

    private static void logger(@NotNull BoaExchange client, @NotNull BoaMiddleware next) {
        long start = System.currentTimeMillis();
        next.yield();
        String method = client.getMethod();
        String path = client.getPath();
        String code = String.valueOf(client.getStatusCode());
        String ms = String.valueOf(System.currentTimeMillis() - start);
        String log = method + " " + path + "  " + code + " (" + ms + "ms)";
        System.out.println(log);
    }
}
