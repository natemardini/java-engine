package start;

import controllers.HomeController;
import engine.BoaEngine;
import engine.BoaExchange;
import engine.Route;
import engine.Scope;

public class Application extends BoaEngine {

    public static void main(String[] args) {
        BoaEngine engine = new Application();
        engine.setPort(8001);
        engine.start();
    }

    @Override
    public void router() {
        new Scope("^/bob",
                new Scope("/ray",
                        get("/(\\w+)/(\\w+)", HomeController::index),
                        post("/\\w+$", HomeController::index),
                        put("/\\w+$", HomeController::index)
                )
        );
    }

    @Override
    public void beforeFilter(BoaExchange client, Route route) {
        super.beforeFilter(client, route);

        System.out.println("Going to " + client.getPath());
    }
}
