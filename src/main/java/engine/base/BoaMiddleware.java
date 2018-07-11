package engine.base;

import java.util.Objects;

public class BoaMiddleware {
    private BoaExchange client;
    private MiddlewareLambda middlewareLambda;
    private BoaMiddleware next;

    private BoaMiddleware() { }

    BoaMiddleware(MiddlewareLambda middleware) {
        this.middlewareLambda = middleware;
    }

    BoaMiddleware(BoaExchange client, MiddlewareLambda middleware) {
        this.client = client;
        this.middlewareLambda = middleware;
    }

    BoaMiddleware(BoaExchange client, MiddlewareLambda middleware, BoaMiddleware next) {
        this.client = client;
        this.middlewareLambda = middleware;
        this.next = next;
    }

    void apply(BoaExchange client) {
        this.client = client;
    }

    public void yield() {
        if (middlewareLambda != null && client != null) {
            middlewareLambda.process(client, Objects.requireNonNullElseGet(next, BoaMiddleware::new));
        }
    }
}
