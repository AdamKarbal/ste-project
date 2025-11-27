package Api.Utils;

import Account.AccountUser;
import Dish.Dish;
import Dish.DishCreateDTO;
import Dish.DishNotFound;
import Order.Order;
import Order.OrderNotFoundException;
import Payment.Payment;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.time.LocalTime;
import Restaurant.Enum.DayOfTheWeek;
import java.util.HashMap;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Requester {
    private final static ObjectMapper mapper=new ObjectMapper().registerModule(new JavaTimeModule());
    public static Dish getDishById(int dishId) throws IOException, InterruptedException {
        try {
            HttpClient client = HttpClient.newHttpClient();
            //System.out.println(dishId);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8004/api/dishRepo/" + dishId+"/"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                throw new DishNotFound("Plat " + dishId + " introuvable.");
            }
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET : code " + response.statusCode());
            }
            Dish dish = mapper.readValue(response.body(),Dish.class);
            //System.out.println(dish.getId());
            return dish;

        } catch (IOException | InterruptedException e) {
            System.out.println(e);
            throw new RuntimeException("Erreur GET require(" + dishId + ")", e);
        }

    }
    public static Restaurant createRestaurant(Restaurant restaurant){
        try {
            String json = mapper.writeValueAsString(restaurant);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8002/api/restaurantrepo/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200) {
                throw new RuntimeException("Erreur POST : code " + response.statusCode());
            }
            return mapper.readValue(response.body(), Restaurant.class);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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

    public static Restaurant getRestaurantByName(String restaurantName){
        try{
            HttpClient client = HttpClient.newHttpClient();
            String encodedName = URLEncoder.encode(restaurantName, StandardCharsets.UTF_8).replace("+", "%20");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8002/api/restaurantrepo/" + encodedName+"/"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new OrderNotFoundException("Restaurant " + encodedName + " introuvable.");
            }
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET : code " + response.statusCode());
            }

            return mapper.readValue(response.body(), Restaurant.class);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur GET require(" + restaurantName + ")", e);
        }
    }

    public static AccountUser getClientById(int id) {
        try {
            String url = "http://localhost:8005/api/accountRepo/" + id+"/";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("⚠️ Client non trouvé. Code HTTP = " + response.statusCode());
                return null;
            }
            return mapper.readValue(response.body(), AccountUser.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AccountUser createUser(AccountUser user){
        try {
            String json = mapper.writeValueAsString(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8005/api/accountRepo/user/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200) {
                throw new RuntimeException("Erreur POST : code " + response.statusCode());
            }
            return mapper.readValue(response.body(), AccountUser.class);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static AccountUser updateCredit(int id, double newCredit) {
        try {
            String url = "http://localhost:8005/api/accountRepo/" + id + "/credit/";

            Map<String, Object> payload = Map.of("credit", newCredit);
            String json = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("PUT", HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            HttpClient httpClient= HttpClient.newHttpClient();
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("⚠️ Erreur updateCredit: " + response.statusCode());
                return null;
            }

            return mapper.readValue(response.body(), AccountUser.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static Order updateOrder(int oldOrderId, Order newOrder) {
        try {
            getOrderById(oldOrderId);
            System.out.println("avant");
            String json = mapper.writeValueAsString(newOrder);
            System.out.println("après");
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
            System.out.println(e);
            throw new RuntimeException("Erreur PUT save(" + oldOrderId + ")", e);
        }
    }

    public static Dish createDish(DishCreateDTO dish){
        try {
            String json = mapper.writeValueAsString(dish);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8004/api/dishRepo/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200) {
                throw new RuntimeException("Erreur POST : code " + response.statusCode());
            }
            return mapper.readValue(response.body(), Dish.class);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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
    /**
     * Enregistrer une commande pour un client :
     * logique métier de :
     *
     *   public void registerOrder(int orderId, Payment payment) {
     *       if (orderHistory.keySet().contains(orderId)) throw new AlreadyExistingOrder("La commande existe déjà");
     *       orderHistory.put(orderId, payment);
     *   }
     *
     * Ici on fait :
     * 1) GET du client
     * 2) On vérifie côté client s'il a déjà cette commande
     * 3) On envoie la mise à jour au service AccountRepo
     *
     * ⚠️ Il faut un endpoint côté AccountRepo, par ex. :
     * POST /api/accountRepo/{accountId}/order/
     * Body : { "orderId": ..., "payment": { ... } }
     */
    public static AccountUser registerOrder(int clientId, int orderId, Payment payment) {
        try {
            // 1) Récupérer le client
            AccountUser client = getClientById(clientId);
            if (client == null) {
                throw new RuntimeException("Client " + clientId + " introuvable");
            }


            // 3) Construire le body à envoyer au microservice AccountRepo
            String url = "http://localhost:8005" + "/api/accountRepo/" + clientId + "/order/"; // <-- à implémenter côté serveur

            Map<String, Object> body = Map.of(
                    "orderId", orderId,
                    "payment", payment  // Jackson sérialisera l'objet Payment
            );
            String jsonBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpClient httpClient=HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new RuntimeException("Erreur lors de registerOrder, code HTTP = " + response.statusCode());
            }

            // On suppose que le service renvoie le AccountUser mis à jour
            return mapper.readValue(response.body(), AccountUser.class);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur réseau lors de registerOrder", e);
        }
    }

    // ======================= SCHEDULES ========================

    private static final String SCHEDULE_BASE_URL = "http://localhost:8007/api/schedule/";

    /**
     * Récupérer tous les créneaux d'un restaurant
     * GET /api/schedule/{restaurant}
     */
    public static List<RestaurantSchedule> getSchedules(String restaurantName) {
        try {
            String url = SCHEDULE_BASE_URL + encode(restaurantName);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET schedules : code " + response.statusCode());
            }

            return mapper.readValue(
                    response.body(),
                    new TypeReference<List<RestaurantSchedule>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException("Erreur GET getSchedules(" + restaurantName + ")", e);
        }
    }


    /**
     * Récupérer les créneaux du jour pour un restaurant
     * GET /api/schedule/{restaurant}/today
     */
    public static List<Map<String, Object>> getTodaySchedules(String restaurantName) {
        try {
            String url = SCHEDULE_BASE_URL + encode(restaurantName) + "/today";
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET today schedules : code " + response.statusCode());
            }

            return mapper.readValue(response.body(),
                    new TypeReference<List<Map<String, Object>>>() {});

        } catch (Exception e) {
            throw new RuntimeException("Erreur GET getTodaySchedules(" + restaurantName + ")", e);
        }
    }

    /**
     * Ajouter un créneau pour un restaurant
     * POST /api/schedule/{restaurant}/add
     * Body : { "day": "...", "start": "HH:mm", "end": "HH:mm", "capacity": ... }
     */
    public static boolean addSchedule(String restaurantName,
                                      DayOfTheWeek day,
                                      LocalTime start,
                                      LocalTime end,
                                      int capacity) {
        try {
            String url = SCHEDULE_BASE_URL + encode(restaurantName) + "/add";

            Map<String, Object> body = new HashMap<>();
            body.put("day", day.name());              // ex: "MONDAY"
            body.put("start", start.toString());      // "HH:mm"
            body.put("end", end.toString());          // "HH:mm"
            body.put("capacity", capacity);

            String json = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 400) {
                throw new RuntimeException("Erreur POST addSchedule : code " + response.statusCode());
            }

            Map<String, Object> result =
                    mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
            Object okVal = result.get("ok");
            return okVal instanceof Boolean && (Boolean) okVal;

        } catch (Exception e) {
            throw new RuntimeException("Erreur POST addSchedule(" + restaurantName + ")", e);
        }
    }

    /**
     * Modifier le jour / horaires d'un créneau
     * PUT /api/schedule/{restaurant}/time/{id}
     * Body : { "day": "...", "start": "HH:mm", "end": "HH:mm" }
     */
    public static boolean updateScheduleTime(String restaurantName,
                                             String scheduleId,
                                             DayOfTheWeek day,
                                             LocalTime start,
                                             LocalTime end) {
        try {
            String url = SCHEDULE_BASE_URL + encode(restaurantName) + "/time/" + scheduleId;

            Map<String, Object> body = new HashMap<>();
            body.put("day", day.name());
            body.put("start", start.toString());
            body.put("end", end.toString());

            String json = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .method("PUT", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 404) {
                throw new RuntimeException("Erreur PUT updateScheduleTime : code " + response.statusCode());
            }

            if (response.statusCode() == 404) {
                return false;
            }

            Map<String, Object> result =
                    mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
            Object okVal = result.get("ok");
            return okVal instanceof Boolean && (Boolean) okVal;

        } catch (Exception e) {
            throw new RuntimeException("Erreur PUT updateScheduleTime(" + restaurantName + "," + scheduleId + ")", e);
        }
    }

    /**
     * Modifier la capacité d'un créneau
     * PUT /api/schedule/{restaurant}/capacity/{id}
     * Body : { "capacity": ... }
     */
    public static boolean updateScheduleCapacity(String restaurantName,
                                                 String scheduleId,
                                                 int capacity) {
        try {
            String url = SCHEDULE_BASE_URL + encode(restaurantName) + "/capacity/" + scheduleId;

            Map<String, Object> body = Map.of("capacity", capacity);
            String json = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .method("PUT", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 404) {
                throw new RuntimeException("Erreur PUT updateScheduleCapacity : code " + response.statusCode());
            }

            if (response.statusCode() == 404) {
                return false;
            }

            Map<String, Object> result =
                    mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
            Object okVal = result.get("ok");
            return okVal instanceof Boolean && (Boolean) okVal;

        } catch (Exception e) {
            throw new RuntimeException("Erreur PUT updateScheduleCapacity(" + restaurantName + "," + scheduleId + ")", e);
        }
    }

    /**
     * Supprimer un créneau
     * DELETE /api/schedule/{restaurant}/{id}
     */
    public static boolean deleteSchedule(String restaurantName, String scheduleId) {
        try {
            String url = SCHEDULE_BASE_URL + encode(restaurantName) + "/" + scheduleId;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 404) {
                throw new RuntimeException("Erreur DELETE schedule : code " + response.statusCode());
            }

            return response.statusCode() == 200;

        } catch (Exception e) {
            throw new RuntimeException("Erreur DELETE deleteSchedule(" + restaurantName + "," + scheduleId + ")", e);
        }
    }

    public static boolean isOpenNow(String restaurantName) {
        try {
            String url = "http://localhost:8007/api/schedule/" + encode(restaurantName) + "/open/now";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET isOpenNow : " + response.statusCode());
            }

            Map<String, Object> data = mapper.readValue(response.body(), Map.class);
            return (boolean) data.get("open");

        } catch (Exception e) {
            throw new RuntimeException("Erreur isOpenNow(" + restaurantName + ")", e);
        }
    }

    public static boolean isOpenAt(String restaurantName, LocalDateTime dateTime) {
        try {
            String dt = dateTime.toString(); // ISO-8601
            String url = "http://localhost:8007/api/schedule/"
                    + encode(restaurantName)
                    + "/open/at/"
                    + dt;

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET isOpenAt : " + response.statusCode());
            }

            Map<String, Object> data = mapper.readValue(response.body(), Map.class);
            return (boolean) data.get("open");

        } catch (Exception e) {
            throw new RuntimeException("Erreur isOpenAt(" + restaurantName + ", " + dateTime + ")", e);
        }
    }

    public static int getScheduleCapacity(String restaurantName, String scheduleId) {
        try {
            String url = "http://localhost:8007/api/schedule/"
                    + encode(restaurantName)
                    + "/capacity/"
                    + scheduleId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new RuntimeException("Schedule introuvable");
            }

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET capacity : code " + response.statusCode());
            }

            Map<String, Object> result =
                    mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});

            return (int) result.get("capacity");

        } catch (Exception e) {
            throw new RuntimeException("Erreur getScheduleCapacity(" + restaurantName + ", " + scheduleId + ")", e);
        }
    }

    /**
     * Petit helper pour encoder proprement le nom du restaurant dans l'URL
     */
    private static String encode(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
