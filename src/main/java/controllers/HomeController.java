package controllers;

import engine.BoaSocket;
import engine.HttpHelpers;

public abstract class HomeController {

    public static void index(BoaSocket client) {
        client.respond(HttpHelpers.CREATED, "Howdy");
    }
}
