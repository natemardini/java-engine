package engine.base;


import engine.controller.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.util.*;
import java.util.stream.Collectors;


public abstract class BoaEngine {

    // FIELDS

    private boolean running = true;
    
    @Getter
    private ServerSocket server;

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
        scanRoutes();
    }

    // LIFE CYCLES

    public void start() {
        try (final ServerSocket server = new ServerSocket(port)) { 
        	this.server = server;
            beforeStart();
            System.out.println("Sweet! We're live on port " + String.valueOf(port) + "!");
            listen();
        } catch (Exception ex) {
            System.err.println("Show stopped cause of: " + ex);
        }
    }

    private void listen() throws Exception {
        while (running) {
        	BoaExchange client = new BoaExchange(server.accept());

        	try {
        		BoaBox box = new BoaBox(this, client);
        		if (multiThreaded) {
                    Thread thread = new Thread(box);
                    thread.start();
                } else {
                    box.run();
                }
        	} catch (Exception e) {
				System.out.println("Mayday: " + e);
			}
        }
        
        System.out.println("Winding down...");
    }

    public void stop() {
        running = false;
    }

    // FILTERS

    public void beforeStart() {

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

    public void defaultErrorResponse(BoaExchange client, List<Route> matchedRoutes) {
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
        client.respond(404, sb.toString());
    }

    // ROUTES

    private void scanRoutes() {
        router();

        for (Class<?> c : ClassIndex.getAnnotated(Controller.class)) {
            String prefix = c.getAnnotation(Controller.class).path();

            Iterable<Method> paths = Arrays.stream(c.getMethods())
                    .filter(m -> m.isAnnotationPresent(Path.class))
                    .collect(Collectors.toSet());

            for (Method p : paths) {
                String path = prefix + p.getAnnotation(Path.class).uri();
                String method = p.getAnnotation(Path.class).method();
                Parameter[] params = p.getParameters();

                IController controller = (BoaExchange cl) -> {
                    List<Object> values = new ArrayList<>();

                    for (Parameter param : params) {
                        if (param.isAnnotationPresent(Param.class)) {
                            values.add(cl.getParam(param.getName()));
                        } else if (param.isAnnotationPresent(Body.class)) {
                            values.add(cl.parseBodyToType(param.getType()));
                        } else if (param.getType() == BoaExchange.class){
                            values.add(cl);
                        }
                    }

                    try {
                        p.invoke(null, values.toArray());
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace(System.err);
                    }
                };

                this.route(method, path, controller);
            }
        }
    }

    public Route[] scope(String path, Route... routes) {
        Arrays.stream(routes).forEach(route -> route.setPath(path + route.getPath()));
        return routes;
    }

    public void router() {
        IController defaultRoute = (BoaExchange client) -> {
            client.respond("<html><p>You're all setup! The Boa liveth!</p></html>");
        };

        get("/", defaultRoute);
    }

    public Route route(List<String> methods, String path, IController controller) {
        Route route = new Route(path, methods, controller);
        routes.add(route);
        return route;
    }

    public Route route(String method, String path, IController controller) {
        List<String> methods = new ArrayList<>();
        methods.add(method);
        return route(methods, path, controller);
    }

    public Route get(String path, IController controller) {
        return route("GET", path, controller);
    }

    public Route post(String path, IController controller) {
        return route("POST", path, controller);
    }

    public Route put(String path, IController controller) {
        return route("PUT", path, controller);
    }

    public Route patch(String path, IController controller) {
        return route("PATCH", path, controller);
    }

    public Route delete(String path, IController controller) {
        return route("DELETE", path, controller);
    }
}
