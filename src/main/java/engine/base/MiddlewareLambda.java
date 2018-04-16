package engine.base;

public interface MiddlewareLambda {
    void process(BoaExchange client, BoaMiddleware next);
}
