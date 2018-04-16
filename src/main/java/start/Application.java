package start;

import engine.base.BoaEngine;
import engine.base.BoaExchange;
import engine.base.BoaMiddleware;

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

    private static void logger(BoaExchange client, BoaMiddleware next) {
        long start = System.currentTimeMillis();
        next.yield();
        String method = client.getMethod();
        String path = client.getPath();
        String code = String.valueOf(client.getStatusCode());
        String ms = String.valueOf(System.currentTimeMillis() - start);
        String log = "INC: " + method + " " + path + "  --  OUT: " + code + " (" + ms + "ms)";
        System.out.println(log);
    }
}
