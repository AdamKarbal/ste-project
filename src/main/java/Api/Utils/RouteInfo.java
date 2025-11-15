package Api.Utils;

import com.sun.net.httpserver.HttpHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteInfo {
    private String method;
    private String path;
    private RouteHandler handler;
    private Pattern pattern;
    public RouteInfo(String method, String path, RouteHandler handler){
        this.method=method;
        this.path=path;
        this.handler=handler;
        String regexPath = path.replaceAll("\\{[^/]+}", "([^/?]+)");
        this.pattern = Pattern.compile(regexPath);
    }
    public boolean matches(String method, String requestPath) {
        return this.method.equalsIgnoreCase(method) && pattern.matcher(requestPath).matches();
    }

    public Matcher getPathMatcher(String requestPath) {
        return pattern.matcher(requestPath);
    }

    public RouteHandler getHandler() {
        return handler;
    }
    public String getMethod(){
        return method;
    }
}

