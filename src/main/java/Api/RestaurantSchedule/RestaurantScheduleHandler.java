package Api.RestaurantSchedule;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Restaurant.RestaurantScheduleManager;
import Restaurant.Enum.DayOfTheWeek;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
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
        ApiRegistry.addRoute("POST", "/api/schedule/{restaurant}/add", this::addSchedule);
        ApiRegistry.addRoute("PUT", "/api/schedule/{restaurant}/capacity/{id}", this::updateCapacity);
        ApiRegistry.addRoute("DELETE", "/api/schedule/{restaurant}/{id}", this::deleteSchedule);
    }

    public void getSchedules(HttpExchange ex, String name) throws IOException {
        var schedules = scheduleManager.getSchedules(name);
        String response = mapper.writeValueAsString(schedules);
        sendResponse(ex, 200, response);
    }

    public void getToday(HttpExchange ex, String name) throws IOException {
        var schedules = scheduleManager.getTodaySchedules(name);
        String response = mapper.writeValueAsString(schedules);
        sendResponse(ex, 200, response);
    }

    public void addSchedule(HttpExchange ex, String name) throws IOException {
        InputStream is = ex.getRequestBody();
        Map<String,Object> body = mapper.readValue(is, Map.class);

        DayOfTheWeek day = DayOfTheWeek.valueOf((String) body.get("day"));
        LocalTime start = LocalTime.parse((String) body.get("start"));
        LocalTime end   = LocalTime.parse((String) body.get("end"));
        int capacity    = (int) body.get("capacity");

        boolean ok = scheduleManager.addSchedule(name, day, start, end, capacity);

        sendResponse(ex, ok ? 200 : 400, "{\"ok\": " + ok + "}");
    }

    public void updateCapacity(HttpExchange ex, String tuple) throws IOException {
        String[] params = tuple.split("/");
        String restaurant = params[0];
        String id = params[1];

        Map<String,Object> body = mapper.readValue(ex.getRequestBody(), Map.class);
        int capacity = (int) body.get("capacity");

        boolean ok = scheduleManager.updateCapacityById(restaurant, id, capacity);

        sendResponse(ex, ok ? 200 : 404, "{\"ok\": " + ok + "}");
    }

    public void deleteSchedule(HttpExchange ex, String tuple) throws IOException {
        String[] params = tuple.split("/");
        String restaurant = params[0];
        String id = params[1];

        boolean ok = scheduleManager.removeScheduleById(restaurant, id);

        sendResponse(ex, ok ? 200 : 404, "{\"ok\": " + ok + "}");
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
                String param = "";
                if (m.find() && m.groupCount() > 0)
                    param = m.group(1);

                route.getHandler().handle(exchange, param);
                return;
            }
        }

        sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
    }
}
