package Api.RestaurantSchedule;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
import Restaurant.RestaurantScheduleManager;
import Restaurant.Enum.DayOfTheWeek;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.Matcher;

import static Api.Utils.Sender.sendResponse;

public class RestaurantScheduleHandler implements HttpHandler {

    private final RestaurantScheduleManager scheduleManager;
    private final ObjectMapper mapper = new ObjectMapper();

    public RestaurantScheduleHandler(RestaurantScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;

        ApiRegistry.addRoute("GET", "/api/schedule/{restaurant}", this::getSchedules);
        ApiRegistry.addRoute("GET", "/api/schedule/{restaurant}/today", this::getToday);
        ApiRegistry.addRoute("GET", "/api/schedule/{restaurant}/capacity/{id}", this::getCapacityById);
        ApiRegistry.addRoute("GET", "/api/schedule/{restaurant}/open/now", this::isOpenNow);
        ApiRegistry.addRoute("GET", "/api/schedule/{restaurant}/open/at/{datetime}", this::isOpenAt);
        ApiRegistry.addRoute("POST", "/api/schedule/{restaurant}/add", this::addSchedule);
        ApiRegistry.addRoute("PUT", "/api/schedule/{restaurant}/time/{id}", this::updateSchedule);
        ApiRegistry.addRoute("PUT", "/api/schedule/{restaurant}/capacity/{id}", this::updateCapacity);
        ApiRegistry.addRoute("DELETE", "/api/schedule/{restaurant}/{id}", this::deleteSchedule);

    }

    public void getSchedules(HttpExchange ex, String... name) throws IOException {
        try{
        var schedules = scheduleManager.getSchedules(name[0]);
        System.out.println(schedules);
        String response = mapper.writeValueAsString(schedules);
        sendResponse(ex, 200, response);}
        catch (Exception e){
            Sender.sendResponse(ex, 500, e.getMessage());
        }
    }

    public void getToday(HttpExchange ex, String... name) throws IOException {
        var schedules = scheduleManager.getTodaySchedules(name[0]);
        String response = mapper.writeValueAsString(schedules);
        sendResponse(ex, 200, response);
    }

    public void getCapacityById(HttpExchange ex, String... params) throws IOException {
        String restaurant = params[0];
        String id = params[1];

        try {
            int capacity = scheduleManager.getCapacityById(restaurant, id);
            sendResponse(ex, 200, "{\"capacity\": " + capacity + "}");
        } catch (Exception e) {
            sendResponse(ex, 404, "{\"error\": \"Schedule not found\"}");
        }
    }

    public void addSchedule(HttpExchange ex, String... name) throws IOException {
        InputStream is = ex.getRequestBody();
        Map<String,Object> body = mapper.readValue(is, Map.class);

        DayOfTheWeek day = DayOfTheWeek.valueOf((String) body.get("day"));
        LocalTime start = LocalTime.parse((String) body.get("start"));
        LocalTime end   = LocalTime.parse((String) body.get("end"));
        int capacity    = (int) body.get("capacity");

        boolean ok = scheduleManager.addSchedule(name[0], day, start, end, capacity);

        sendResponse(ex, ok ? 200 : 400, "{\"ok\": " + ok + "}");
    }

    public void updateSchedule(HttpExchange ex, String... params) throws IOException {
        String restaurant = params[0]; // {restaurant}
        String id = params[1];         // {id}

        Map<String,Object> body = mapper.readValue(ex.getRequestBody(), Map.class);

        String dayStr   = (String) body.get("day");
        String startStr = (String) body.get("start");
        String endStr   = (String) body.get("end");

        if (dayStr == null || startStr == null || endStr == null) {
            sendResponse(ex, 400, "{\"error\":\"Missing field: day, start or end\"}");
            return;
        }

        DayOfTheWeek targetDay;
        try {
            targetDay = DayOfTheWeek.valueOf(dayStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendResponse(ex, 400, "{\"error\":\"Invalid day value\"}");
            return;
        }

        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(startStr); // format "HH:mm"
            end   = LocalTime.parse(endStr);
        } catch (Exception e) {
            sendResponse(ex, 400, "{\"error\":\"Invalid time format (expected HH:mm)\"}");
            return;
        }

        boolean ok = scheduleManager.updateScheduleById(restaurant, id, targetDay, start, end);

        sendResponse(ex, ok ? 200 : 404, "{\"ok\": " + ok + "}");
    }


    public void updateCapacity(HttpExchange ex, String... params) throws IOException {
        String restaurant = params[0];
        String id = params[1];

        Map<String,Object> body = mapper.readValue(ex.getRequestBody(), Map.class);
        int capacity = (int) body.get("capacity");

        boolean ok = scheduleManager.updateCapacityById(restaurant, id, capacity);

        sendResponse(ex, ok ? 200 : 404, "{\"ok\": " + ok + "}");
    }

    public void deleteSchedule(HttpExchange ex, String... params) throws IOException {
        String restaurant = params[0];
        String id = params[1];

        boolean ok = scheduleManager.removeScheduleById(restaurant, id);

        sendResponse(ex, ok ? 200 : 404, "{\"ok\": " + ok + "}");
    }

    public void isOpenNow(HttpExchange ex, String... params) throws IOException {
        String restaurantName = params[0];

        boolean open = scheduleManager.isOpenNow(restaurantName);

        sendResponse(ex, 200, "{\"open\": " + open + "}");
    }

    public void isOpenAt(HttpExchange ex, String... params) throws IOException {
        String restaurantName = params[0];
        String dateTimeString = params[1];

        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeString);
        } catch (DateTimeParseException e) {
            sendResponse(ex, 400, "{\"error\":\"Invalid datetime format, expected ISO: yyyy-MM-ddTHH:mm\"}");
            return;
        }

        boolean open = scheduleManager.isOpenAt(restaurantName, dateTime);

        sendResponse(ex, 200, "{\"open\": " + open + "}");
    }




    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        for (RouteInfo route : ApiRegistry.getRoutes()) {
            if (route.matches(method, path)) {
                Matcher m = route.getPathMatcher(path);

                String[] params = new String[0];
                if (m.find() && m.groupCount() > 0) {
                    params = new String[m.groupCount()];
                    for (int i = 0; i < m.groupCount(); i++) {
                        params[i] = m.group(i + 1);
                    }
                }

                route.getHandler().handle(exchange, params);
                return;
            }
        }

        sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
    }
}
