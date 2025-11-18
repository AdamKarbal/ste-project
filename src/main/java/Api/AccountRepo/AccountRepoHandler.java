package Api.AccountRepo;

import Account.AccountRepo;
import Account.Account;
import Account.AccountUser;
import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
import Payment.Payment;
import Account.AccountUser;
import Order.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class AccountRepoHandler implements HttpHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private AccountRepo repo;
    public AccountRepoHandler(AccountRepo repo){
        this.repo=repo;
        ApiRegistry.addRoute("POST","/api/accountRepo/user/",this::addAccountUser);
        ApiRegistry.addRoute("GET","/api/accountRepo/{accountId}/",this::getAccount);
        ApiRegistry.addRoute("PUT","/api/accountRepo/{accountId}/credit/",this::updateCredit);
        ApiRegistry.addRoute("POST", "/api/accountRepo/{clientId}/order/", this::registerOrder);
    }
    private void getAccount(HttpExchange exchange,String... params) throws IOException {
        try{
            int id = Integer.parseInt(params[0]);
            Optional<Account> account = repo.findById(id);
            if (account.isEmpty()) {
                Sender.sendResponse(exchange, 404, "Account not found");
                return;
            }

            Account account1 = account.get();
            String responseJson = mapper.writeValueAsString(account1);
            Sender.sendResponse(exchange, 200, responseJson);
        }
        catch(Exception e){
            System.out.println(e);
            Sender.sendResponse(exchange,500,"Erreur");
        }
    }

    private void addAccountUser(HttpExchange exchange,String... params){
        try{
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String,Object> attributes = mapper.readValue(body,Map.class);
            String name = (String) attributes.get("name");
            if(name==null){
                Sender.sendResponse(exchange,400,"Nom manquant");
            }
            AccountUser user;
            if(attributes.containsKey("credit")){
                user=repo.save(new AccountUser(name,(double)attributes.get("credit")));
            }
            else{
                user=repo.save(new AccountUser(name));
            }

            Sender.sendResponse(exchange,200,mapper.writeValueAsString(user));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCredit(HttpExchange exchange, String... pathParam) throws IOException {
        try {
            int clientId = Integer.parseInt(pathParam[0]);

            Optional<Account> client = repo.findById(clientId);
            if (client.isEmpty()) {
                Sender.sendResponse(exchange, 404, "Client not found");
                return;
            }
            AccountUser client1 = (AccountUser) client.get();
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, Object> data = mapper.readValue(body, Map.class);

            double newCredit = ((Number) data.get("credit")).doubleValue();

            client1.setCredit(newCredit);

            Sender.sendResponse(exchange, 200, mapper.writeValueAsString(client1));

        } catch (Exception e) {
            e.printStackTrace();
            Sender.sendResponse(exchange, 500, "Error updating credit");
        }
    }

    public void registerOrder(HttpExchange exchange, String... params) throws IOException {
        try {
            int clientId = Integer.parseInt(params[0]);
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, Object> data = mapper.readValue(body, Map.class);
            int orderId = (int) data.get("orderId");
            Payment payment = mapper.convertValue(data.get("payment"), Payment.class);
            Optional<Account> client1 = repo.findById(clientId);
            if (client1.isEmpty()) {
                Sender.sendResponse(exchange, 404, "Client not found");
                return;
            }
            AccountUser client= (AccountUser) client1.get();
            // 5) Vérifier si la commande existe déjà
            if (client.getOrderHistory().containsKey(orderId)) {
                Sender.sendResponse(exchange, 409, "Order already exists for this client");
                return;
            }
            client.registerOrder(orderId, payment);
            repo.save(client);
            String responseJson = mapper.writeValueAsString(client);
            Sender.sendResponse(exchange, 200, responseJson);

        } catch (Exception e) {
            e.printStackTrace();
            Sender.sendResponse(exchange, 500, "Internal server error while registering order");
        }
    }



    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            Sender.sendResponse(exchange, 204, "");
            return;
        }
        for (RouteInfo route : ApiRegistry.getRoutes()) {
            if (route.matches(requestMethod, requestPath)) {
                Matcher matcher = route.getPathMatcher(requestPath);
                String param = "";
                if (matcher.find() && matcher.groupCount()>0) {
                    param = matcher.group(1);
                }

                route.getHandler().handle(exchange, param);
                return;
            }
        }
    }
}
