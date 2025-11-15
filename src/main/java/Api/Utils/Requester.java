package Api.Utils;

import Dish.Dish;
import Order.Order;
import Order.OrderNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Requester {
    private final static ObjectMapper mapper=new ObjectMapper();
    public static Dish getDishById(int dishId) throws IOException, InterruptedException {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8004/api/dishRepo/" + dishId+"/"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                throw new OrderNotFoundException("Plat " + dishId + " introuvable.");
            }
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET : code " + response.statusCode());
            }
            Dish dish = mapper.readValue(response.body(),Dish.class);
            System.out.println(dish.getId());
            return dish;

        } catch (IOException | InterruptedException e) {
            System.out.println(e);
            throw new RuntimeException("Erreur GET require(" + dishId + ")", e);
        }

    }
    public static Order getOrderById(int orderId) { // requête GET
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8003/api/orderRepo/" + orderId+"/"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new OrderNotFoundException("Commande " + orderId + " introuvable.");
            }
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET : code " + response.statusCode());
            }

            return mapper.readValue(response.body(), Order.class);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur GET require(" + orderId + ")", e);
        }
    }

    public static Order updateOrder(int oldOrderId, Order newOrder) {
        try {
            getOrderById(oldOrderId);
            String json = mapper.writeValueAsString(newOrder);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8003/api/orderRepo/" + oldOrderId+"/"))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur PUT : code " + response.statusCode());
            }
            return mapper.readValue(response.body(), Order.class);

        } catch (Exception e) {
            throw new RuntimeException("Erreur PUT save(" + oldOrderId + ")", e);
        }
    }

    public static Order createOrder(Order order) {
        try {
            String json = mapper.writeValueAsString(order);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8003/api/orderRepo/"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200) {
                throw new RuntimeException("Erreur POST : code " + response.statusCode());
            }
            Order order1 =mapper.readValue(response.body(), Order.class);
            return order1;

        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException();
        }
    }

    public static boolean deleteOrder(int orderId) {
        try {
            getOrderById(orderId);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8003/api/orderRepo/" + orderId+"/"))
                    .DELETE()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new OrderNotFoundException("Commande " + orderId + " introuvable.");
            }

            if (response.statusCode() != 200 && response.statusCode() != 204) {
                throw new RuntimeException("Erreur DELETE : code " + response.statusCode());
            }

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Erreur DELETE delete(" + orderId + ")", e);
        }
    }
}
