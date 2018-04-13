package engine;

import engine.BoaSocket;
import framework.Controller;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Route {

    @Getter @Setter
    private String path = "^/////";

    @Getter
    private List<String> methods = new ArrayList<>();

    @Getter
    private Controller controller;

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
}
