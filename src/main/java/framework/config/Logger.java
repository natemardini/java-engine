package framework.config;

import engine.base.BoaExchange;
import engine.base.BoaMiddleware;
import org.jetbrains.annotations.NotNull;

public abstract class Logger {

    public static void standard(@NotNull BoaExchange client, @NotNull BoaMiddleware next) {
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
