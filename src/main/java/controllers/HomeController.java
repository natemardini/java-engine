package controllers;

import engine.BoaExchange;
import engine.HttpHelpers;

public abstract class HomeController {

    public static void index(BoaExchange client) {
        client.respond(HttpHelpers.CREATED, "Howdy");
    }
}
