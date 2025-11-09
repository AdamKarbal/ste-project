package Api.Utils;

import com.sun.net.httpserver.HttpHandler;

import java.util.List;

public class ApiRegistry {
    private static List<RouteInfo> routes;
    public static void addRoute(String method, String path, HttpHandler handler){
        routes.add(new RouteInfo(method, path, handler));
    }
    public static List<RouteInfo> getRoutes(){
        return routes;
    }
}
