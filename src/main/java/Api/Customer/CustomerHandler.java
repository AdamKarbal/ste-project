package Api.Customer;

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

        System.out.println("Requête reçue : " + method + " " + path);

        if("GET".equalsIgnoreCase(method)) {
            String response = "Bienvenu dans le Customer Service";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());

        }
        else{
            exchange.sendResponseHeaders(404, 0);
        }

        exchange.close();
    }


}