package Api.Utils;

import com.sun.net.httpserver.HttpHandler;

import java.util.ArrayList;
import java.util.List;

public class ApiRegistry {
    private static List<RouteInfo> routes= new ArrayList<>();
    public static void addRoute(String method, String path, RouteHandler handler){
        routes.add(new RouteInfo(method, path, handler));
    }
    public static List<RouteInfo> getRoutes(){
        return routes;
    }
}
