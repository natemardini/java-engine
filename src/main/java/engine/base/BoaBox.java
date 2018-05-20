package engine.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import engine.controller.Route;


public class BoaBox implements Runnable {

    private BoaEngine engine;
    private BoaExchange client;
    private List<BoaMiddleware> middlewares = new ArrayList<>();

    BoaBox(BoaEngine engine, BoaExchange client) {
        this.engine = engine;
        this.client = client;
        this.engine.onRequest(this.client);
        organizeMiddleware();
    }

    @Override
    public void run() {
    	if (middlewares.size() > 0 && middlewares.get(0) != null) {
            middlewares.get(0).yield();
        }

    	close();
    }

    private void grabParamsFromRequestPath(@NotNull BoaExchange client, @NotNull Route route) {
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

        for (int i = engine.getMiddlewareLambdas().size() - 1; i >= 0; i--) {
            MiddlewareLambda lambda = engine.getMiddlewareLambdas().get(i);
            BoaMiddleware middleware = new BoaMiddleware(client, lambda, next);
            middlewares.add(middleware);
            next = middleware;
        }

        Collections.reverse(middlewares);
    }

    private void mapRoute(BoaExchange client, BoaMiddleware _nx) {
        List<Route> matchedRoutes = new ArrayList<>();

        engine.getRoutes().forEach(route -> {
            if (client.getPath().matches(route.getPath()) && route.getMethods().contains(client.getMethod())) {
                matchedRoutes.add(route);
            }
        });

        if (matchedRoutes.size() == 1) {
            Route matchedRoute = matchedRoutes.get(0);
            grabParamsFromRequestPath(client, matchedRoute);
            engine.beforeFilter(client, matchedRoute);
            matchedRoute.getController().negotiate(client);
            engine.afterFilter(client, matchedRoute);
        } else {
            engine.defaultErrorResponse(client, matchedRoutes);
        }
    }

	public void close() {
		client.close();
	}
}
