package Web.Customer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;


public class CustomerHandler implements HttpHandler {
//...
    @Override
    public void handle(HttpExchange exchange) throws IOException {
//...
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
//...
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        //exchange.sendResponseHeaders(200, response.length());
        //exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

}