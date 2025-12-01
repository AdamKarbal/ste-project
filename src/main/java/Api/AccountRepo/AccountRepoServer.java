package Api.AccountRepo;

import Account.AccountRepo;
import Account.AccountUser;
import Api.OrderRepo.OrderRepoHandler;
import Dish.Dish;
import Dish.DishRepo;
import Order.OrderRepo;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class AccountRepoServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException{
        int port = 8005;
        String path = "/api/accountRepo/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        AccountRepo repo = new AccountRepo();
        server.createContext(path, new AccountRepoHandler(repo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ AccountRepo en ligne sur http://localhost:" + port + path);
        return  server;
    }
}
