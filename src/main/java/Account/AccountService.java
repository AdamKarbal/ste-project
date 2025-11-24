package Account;

import Payment.Payment;
import Payment.AlreadyExistingOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Service côté client (ou côté autre microservice)
 * qui parle au service AccountRepo via HTTP.
 */
public class AccountService {

    private static final String BASE_URL = "http://localhost:8005"; // adapte le port si besoin

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public AccountService() {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    /**
     * Créer un nouveau client (AccountUser) via :
     * POST /api/accountRepo/user/
     * Body JSON : { "name": "..." }
     */
    public AccountUser createClient(String name) {
        try {
            String url = BASE_URL + "/api/accountRepo/user/";

            Map<String, Object> body = Map.of("name", name);
            String jsonBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new RuntimeException("Erreur lors de la création du client, code HTTP = " + response.statusCode());
            }

            return mapper.readValue(response.body(), AccountUser.class);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur réseau lors de createClient", e);
        }
    }

    /**
     * Récupérer un client par son id via :
     * GET /api/accountRepo/{accountId}/
     */
    public AccountUser getClientById(int clientId) {
        try {
            String url = BASE_URL + "/api/accountRepo/" + clientId + "/";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return null;
            }

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur lors du getClientById, code HTTP = " + response.statusCode());
            }

            return mapper.readValue(response.body(), AccountUser.class);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur réseau lors de getClientById", e);
        }
    }

    /**
     * Ajouter de l'argent au crédit d'un client :
     * 1) GET pour récupérer le client
     * 2) calculer le nouveau crédit
     * 3) PUT /api/accountRepo/{accountId}/credit/ avec le nouveau crédit
     *
     * ⚠️ J'assume que ton endpoint updateCredit attend un body JSON
     * du type : { "credit": <nouvelle_valeur> }
     */
    public AccountUser addCredit(int clientId, double amount) {
        try {
            AccountUser client = getClientById(clientId);
            if (client == null) {
                throw new RuntimeException("Client " + clientId + " introuvable");
            }

            double newCredit = client.getCredit() + amount;

            String url = BASE_URL + "/api/accountRepo/" + clientId + "/credit/";

            Map<String, Object> body = Map.of("credit", newCredit);
            String jsonBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur lors de addCredit, code HTTP = " + response.statusCode());
            }

            // On renvoie le client mis à jour renvoyé par le service
            return mapper.readValue(response.body(), AccountUser.class);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur réseau lors de addCredit", e);
        }
    }


}
