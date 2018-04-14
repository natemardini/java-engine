package engine.base;


import engine.connection.BoaExchange;
import engine.controller.Controller;
import engine.controller.Route;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.net.ServerSocket;
import java.util.*;


public abstract class BoaEngine {

    // FIELDS

    private boolean running = true;

    @Getter @Setter
    private boolean multiThreaded = true;

    @Getter(AccessLevel.PACKAGE)
    private List<Route> routes = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private List<MiddlewareLambda> middlewareLambdas = new ArrayList<>();

    @Getter @Setter
    private int port = 8000;

    // CONSTRUCTOR

    public BoaEngine() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> running = false));
        router();
    }

    // LIFE CYCLES

    public void start() {
        try {
            final ServerSocket server = new ServerSocket(port);
            beforeStart(server);
            System.out.println("Sweet! We're live on port " + String.valueOf(port) + "!");
            listen(server);
        } catch (Exception ex) {
            System.err.println("Show stopped cause of: " + ex);
        }
    }

    private void listen(@NonNull @NotNull ServerSocket server) throws Exception {
        while (running) {
            BoaExchange client = new BoaExchange(server.accept());
            BoaBox box = new BoaBox(this, client);
            if (multiThreaded) {
                Thread thread = new Thread(box);
                thread.start();
            } else {
                box.run();
            }
        }
        System.out.println("Winding down...");
        server.close();
    }

    public void stop() {
        running = false;
    }

    // FILTERS

    public void beforeStart(ServerSocket server) {

    }

    public void onRequest(BoaExchange client) {

    }

    public void beforeFilter(BoaExchange client, Route route) {

    }


    public void afterFilter(BoaExchange client, Route route) {}

    // MIDDLEWARE

    public void use(MiddlewareLambda lambda) {
        middlewareLambdas.add(lambda);
    }


    // ROUTING



    void defaultErrorResponse(BoaExchange client, List<Route> matchedRoutes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><p>");
        sb.append(client.getMethod());
        sb.append(" ");
        sb.append(client.getPath());

        if (matchedRoutes.size() > 1) {
            sb.append(" matched too many routes.</p></html>");
        } else {
            sb.append(" matched no route.</p></html>");
        }
        client.respond(400, sb.toString());
    }

    // ROUTES

    public Route[] scope(String path, Route... routes) {
        Arrays.stream(routes).forEach(route -> route.setPath(path + route.getPath()));
        return routes;
    }

    public void router() {
        Controller defaultRoute = (BoaExchange client) -> {
            client.respond("<html><p>You're all setup! The Boa liveth!</p></html>");
        };

        get("/", defaultRoute);
    }

    public Route route(List<String> methods, String path, Controller controller) {
        Route route = new Route(path, methods, controller);
        routes.add(route);
        return route;
    }

    public Route route(String method, String path, Controller controller) {
        List<String> methods = new ArrayList<>();
        methods.add(method);
        return route(methods, path, controller);
    }

    public Route get(String path, Controller controller) {
        return route("GET", path, controller);
    }

    public Route post(String path, Controller controller) {
        return route("POST", path, controller);
    }

    public Route put(String path, Controller controller) {
        return route("PUT", path, controller);
    }

    public Route patch(String path, Controller controller) {
        return route("PATCH", path, controller);
    }

    public Route delete(String path, Controller controller) {
        return route("DELETE", path, controller);
    }
}
