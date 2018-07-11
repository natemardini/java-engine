package framework;

import engine.base.BoaEngine;
import framework.config.Logger;


public class Application extends BoaEngine {

    public static void main(String[] args) {
        BoaEngine engine = new Application();

        engine.setPort(8001);
        engine.use(Logger::standard);

        engine.start();
    }
}
