package engine.controller;

import engine.connection.BoaExchange;

public interface Controller {
    void negotiate(BoaExchange socket);
}
