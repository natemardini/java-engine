package engine.base;

import engine.connection.BoaExchange;

public interface MiddlewareLambda {
    void process(BoaExchange client, BoaMiddleware next);
}
