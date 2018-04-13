package start;

import controllers.HomeController;
import engine.BoaEngine;
import engine.Route;
import engine.RouteGroup;

public class Application extends BoaEngine {

    public static void main(String[] args) {
        BoaEngine engine = new Application();
        engine.setPort(8001);
        engine.start();
    }

    @Override
    public void router() {
        new RouteGroup("^/bob",
                new RouteGroup("/ray",
                        get("/(\\w+)/(\\w+)", HomeController::index),
                        post("/\\w+$", HomeController::index),
                        put("/\\w+$", HomeController::index)
                )
        );
    }
}
