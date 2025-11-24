package Api.Utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

@FunctionalInterface
public interface RouteHandler {
    void handle(HttpExchange exchange, String... param) throws IOException;
}
