package start;

import jdk.nashorn.api.tree.RegExpLiteralTree;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Route {
    private String path = "^/////";
    private List<String> methods = new ArrayList<>();
    private Controller controller = BoaSocket::respond;

    public Route(String path, List<String> methods, Controller controller) {
        this.path = path;
        this.methods = methods;
        this.controller = controller;
    }

    public Route(String path, String method, Controller controller) {
        this.path = path;
        this.methods.add(method);
        this.controller = controller;
    }

    public String getPath() {
        return path;
    }

    public List<String> getMethods() {
        return methods;
    }

    public Controller getController() {
        return controller;
    }
}
