package engine;

import lombok.Getter;


public class Scope {

    @Getter
    private String path;

    @Getter
    private Route[] routes;

    public Scope(String path, Route... routes) {
        this.path = path;
        this.routes = routes;

        for (Route route : this.getRoutes()) {
            route.setPath(this.getPath() + route.getPath());
        }
    }

    public Scope(String path, Scope group) {
        this.path = path;
        this.routes = group.getRoutes();

        for (Route route : this.getRoutes()) {
            route.setPath(this.getPath() + route.getPath());
        }
    }
}
