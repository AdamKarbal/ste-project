package Api.AccountRepo;

import Account.AccountRepo;
import Account.Account;
import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
import Order.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;

public class AccountRepoHandler implements HttpHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private AccountRepo repo;
    public AccountRepoHandler(AccountRepo repo){
        this.repo=repo;
        ApiRegistry.addRoute("POST","/api/accountRepo/",this::addAccount);
        ApiRegistry.addRoute("GET","/api/accountRepo/{accountId}/",this::getAccount);
    }
    private void getAccount(HttpExchange exchange,String... params) throws IOException {
        try{
            System.out.println("Geyt appelé");
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

    private void addAccount(HttpExchange exchange,String... params){

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET");
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
