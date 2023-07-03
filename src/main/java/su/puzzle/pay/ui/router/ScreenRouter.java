package su.puzzle.pay.ui.router;

import java.util.*;

public class ScreenRouter {
    public LinkedHashMap<String, Route> routes = new LinkedHashMap<>();

    public ScreenRouter add_route(String name, Route route) {
        routes.put(name, route);

        return this;
    }

    public void route(String name) {
        routes.get(name).route(new Context(name, this));
    }

    public <T> void route(String name, T props) {
        routes.get(name).route(new Context(name, this), props);
    }
}
