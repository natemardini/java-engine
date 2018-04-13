package start;


import com.google.gson.Gson;
import edu.emory.mathcs.backport.java.util.Arrays;
import lombok.Setter;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Application {

    private List<Route> routes = new ArrayList<>();

    public static void main(String[] args) {
        new Application(8000);
    }

    public void mapRoutes() {
        get("^/\\w+$", HomeController::index);
    }

    public void findRoute(BoaSocket client) {
        List<Route> matchedRoutes = new ArrayList<>();

        routes.forEach(route -> {
            if (client.getPath().matches(route.getPath()) && route.getMethods().contains(client.getMethod())) {
                matchedRoutes.add(route);
            }
        });

        if (matchedRoutes.size() == 1) {
            Route match = matchedRoutes.get(0);
            match.getController().negotiate(client);
        } else {
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
    }

    private Application(int port) {
        mapRoutes();

        try {
            final ServerSocket server = new ServerSocket(port);
            System.out.println("Sweet! We're live on port 8080!");
            this.listen(server);
        } catch (Exception ex) {
            System.err.println("Show stopped cause of: " + ex);
        }
    }

    public void route(String method, String path, Controller controller) {
        List<String> methods = new ArrayList<>();
        methods.add(method);
        route(methods, path, controller);
    }

    public void route(List<String> methods, String path, Controller controller) {
        routes.add(new Route(path, methods, controller));
    }

    public void get(String path, Controller controller) {
        route("GET", path, controller);
    }

    public void post(String path, Controller controller) {
        route("POST", path, controller);
    }

    private void listen(ServerSocket server) throws Exception {
        while (true) {
            try (BoaSocket client = new BoaSocket(server.accept())) {
                findRoute(client);
            }
        }
    }
}
