package engine;

import lombok.Getter;


public class RouteGroup {

    @Getter
    private String path;

    @Getter
    private Route[] routes;

    public RouteGroup(String path, Route... routes) {
        this.path = path;
        this.routes = routes;

        for (Route route : this.getRoutes()) {
            route.setPath(this.getPath() + route.getPath());
        }
    }

    public RouteGroup(String path, RouteGroup group) {
        this.path = path;
        this.routes = group.getRoutes();

        for (Route route : this.getRoutes()) {
            route.setPath(this.getPath() + route.getPath());
        }
    }
}
