package controllers;

import engine.connection.BoaExchange;

public abstract class HomeController {

    public static void index(BoaExchange client) {
        client.render("index");
    }
}
