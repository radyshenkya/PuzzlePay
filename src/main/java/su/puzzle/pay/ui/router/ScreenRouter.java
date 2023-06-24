package su.puzzle.pay.ui.router;

import su.puzzle.pay.api.exceptions.*;

import java.util.LinkedHashMap;

public class ScreenRouter {
    public LinkedHashMap<String, Route> routes = new LinkedHashMap<>();

    public ScreenRouter add_route(String name, Route route) {
        routes.put(name, route);

        return this;
    }

    public void route(String name) throws ApiCallException, ApiResponseException {
        routes.get(name).route(new Context(name, this));
    }

    public <T> void route(String name, T props) throws ApiCallException, ApiResponseException {
        routes.get(name).route(new Context(name, this), props);
    }
}
