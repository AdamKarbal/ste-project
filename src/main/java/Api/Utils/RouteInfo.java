package Api.Utils;

import com.sun.net.httpserver.HttpHandler;

import java.util.regex.Pattern;

public class RouteInfo {
    private String method;
    private String path;
    private HttpHandler handler;
    private Pattern pattern;
    public RouteInfo(String method, String path, HttpHandler handler){
        this.method=method;
        this.path=path;
        this.handler=handler;
        String regexPath = path.replaceAll("\\{[^/]+}", "([^/?]+)");
        this.pattern = Pattern.compile(regexPath);
    }
}
