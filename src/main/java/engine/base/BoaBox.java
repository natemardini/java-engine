package engine.base;

import engine.connection.BoaExchange;
import engine.controller.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoaBox implements Runnable {

    private BoaEngine server;
    private BoaExchange client;
    private List<BoaMiddleware> middlewares = new ArrayList<>();

    BoaBox(BoaEngine server, BoaExchange client) {
        this.server = server;
        this.client = client;
        server.onRequest(this.client);
        organizeMiddleware();
    }

    @Override
    public void run() {
        runMiddleware();
    }

    private void grabParamsFromRequestPath(BoaExchange client, Route route) {
        Pattern p = Pattern.compile(route.getPath());
        Matcher m = p.matcher(client.getPath());

        while (m.find()) {
            for (String key : route.getParamNames()) {
                client.getParams().put(key, m.group(key));
            }
        }
    }

    private void organizeMiddleware() {
        BoaMiddleware next = new BoaMiddleware(client, this::mapRoute);
        middlewares.add(next);

        for (int i = server.getMiddlewareLambdas().size() - 1; i >= 0; i--) {
            MiddlewareLambda lambda = server.getMiddlewareLambdas().get(i);
            BoaMiddleware middleware = new BoaMiddleware(client, lambda, next);
            middlewares.add(middleware);
            next = middleware;
        }

        Collections.reverse(middlewares);
    }

    private void runMiddleware() {
        if (middlewares.size() > 0 && middlewares.get(0) != null) {
            middlewares.get(0).yield();
        }
    }

    private void mapRoute(BoaExchange client, BoaMiddleware _nx) {
        List<Route> matchedRoutes = new ArrayList<>();

        server.getRoutes().forEach(route -> {
            if (client.getPath().matches(route.getPath()) && route.getMethods().contains(client.getMethod())) {
                matchedRoutes.add(route);
            }
        });

        if (matchedRoutes.size() == 1) {
            Route matchedRoute = matchedRoutes.get(0);
            grabParamsFromRequestPath(client, matchedRoute);
            server.beforeFilter(client, matchedRoute);
            matchedRoute.getController().negotiate(client);
            server.afterFilter(client, matchedRoute);
        } else {
            server.defaultErrorResponse(client, matchedRoutes);
        }
    }
}
