package steps;

import Api.Utils.Requester;
import Dish.*;
import Dish.suggest.Suggestion;
import Restaurant.*;
import Restaurant.RestaurantManager;
import Restaurant.Exception.DishAlreadyInDishes;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public class R4aSteps {
    private final RestaurantManager manager = new RestaurantManager();
    private DishInput input;
    private Dish created;
    private Suggestion suggestion;
    private Exception caught;
    private String currentRestaurantName; // Nom du restaurant en cours de test

    @Given("un RestaurantManager initialisé pour {string}")
    public void unRestaurantManagerInitialisePour(String name) {
        currentRestaurantName = name;
        Restaurant restaurant = new Restaurant.Builder()
                .name(name)
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .cuisineType(new ArrayList<>())
                .establishmentType(null)
                .build();

        Requester.createRestaurant(restaurant);
    }

    @And("un référentiel de suggestions")
    public void unReferentielDeSuggestions(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        assertFalse(rows.isEmpty(), "Le référentiel de suggestions ne doit pas être vide");
    }


    @When("je prévisualise la suggestion pour {string}")
    public void jePrevisualiseLaSuggestionPour(String name) {
        suggestion = manager.previewSuggestion(name);
        System.out.println(suggestion);
    }

    @Then("la catégorie suggérée est {string}")
    public void laCategorieSuggereeEst(String cat) {
        assertNotNull(suggestion, "Aucune suggestion");
        assertEquals(DishCategory.valueOf(cat), suggestion.category);
    }

    @Then("le type suggéré est {string}")
    public void leTypeSuggereEst(String type) {
        assertNotNull(suggestion, "Aucune suggestion");
        assertEquals(DishType.valueOf(type), suggestion.type);
    }

    @Then("un prix moyen est proposé")
    public void unPrixMoyenEstPropose() {
        assertNotNull(suggestion, "Aucune suggestion");
        assertNotNull(suggestion.suggestedPrice, "Pas de prix suggéré");
    }

    @Then("les toppings suggérés contiennent {string}")
    public void lesToppingsSuggérésContiennent(String toppingName) {
        assertNotNull(suggestion.toppings, "Aucun topping suggéré");
        assertTrue(
                suggestion.toppings.stream().anyMatch(t -> t.getName().equalsIgnoreCase(toppingName)),
                "Topping suggéré manquant: " + toppingName
        );
    }

    @Then("les tags suggérés contiennent {string}")
    public void lesTagsSuggérésContiennent(String tagName) {
        assertNotNull(suggestion.tags, "Aucun tag suggéré");
        DishTag expected = DishTag.valueOf(tagName);
        assertTrue(suggestion.tags.contains(expected), "Tag suggéré manquant: " + tagName);
    }

    @When("je saisis un plat avec")
    public void jeSaisisUnPlatAvec(io.cucumber.datatable.DataTable table) {
        input = new DishInput();
        for (List<String> row : table.asLists()) {
            if (row.isEmpty()) continue;
            String key = row.get(0).trim();
            String value = (row.size() > 1) ? row.get(1).trim() : "";
            applyField(input, key, value);
        }
    }

    private void applyField(DishInput in, String key, String value) {
        switch (key.toLowerCase(Locale.ROOT)) {
            case "name" -> in.name = value;
            case "description" -> in.description = value;
            case "category" -> in.category = DishCategory.valueOf(value);
            case "type" -> in.type = DishType.valueOf(value);
            case "price" -> in.price = Double.valueOf(value);
            case "tags" -> in.tags = parseTags(value);
            case "toppings" -> in.toppings = parseToppings(value);
            default -> throw new IllegalArgumentException("Champ inconnu dans DishInput: " + key);
        }
    }

    private List<DishTag> parseTags(String csvOrSemi) {
        if (csvOrSemi == null || csvOrSemi.isBlank()) return List.of();
        return Arrays.stream(csvOrSemi.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(DishTag::valueOf)
                .collect(Collectors.toList());
    }

    private List<DishTopping> parseToppings(String spec) {
        if (spec == null || spec.isBlank()) return List.of();
        List<DishTopping> list = new ArrayList<>();
        for (String part : spec.split("[,;]")) {
            String p = part.trim();
            if (p.isEmpty()) continue;
            String[] kv = p.split(":");
            String name = kv[0].trim();
            float price = (kv.length > 1) ? Float.parseFloat(kv[1].trim()) : 0f;
            list.add(new DishTopping(name, price));
        }
        return list;
    }

    @When("je crée le plat via le RestaurantManager")
    public void jeCreeLePlatViaLeRestaurantManager() throws Exception {
        caught = null;
        created = manager.createDish(input, currentRestaurantName);
    }

    @When("je tente de créer le plat via le RestaurantManager")
    public void jeTenteDeCreerLePlatViaLeRestaurantManager() {
        try {
            created = manager.createDish(input,currentRestaurantName);
        } catch (Exception ex) {
            caught = ex;
        }
    }


    @Then("le plat créé a pour type {string}")
    public void lePlatCreéAPourType(String type) {
        assertNotNull(created, "Plat non créé");
        assertEquals(DishType.valueOf(type), created.getType());
    }

    @Then("le plat créé a pour nom {string}")
    public void lePlatCreeAPourNom(String expected) {
        assertNotNull(created, "Plat non créé");
        assertEquals(expected, created.getName());
    }

    @Then("la catégorie finale est {string}")
    public void laCategorieFinaleEst(String cat) {
        assertNotNull(created, "Plat non créé");
        assertEquals(DishCategory.valueOf(cat), created.getCategory());
    }

    @Then("le type final est {string}")
    public void leTypeFinalEst(String type) {
        assertNotNull(created, "Plat non créé");
        assertEquals(DishType.valueOf(type), created.getType());
    }

    @Then("le prix final est {string}")
    public void lePrixFinalEst(String priceStr) {
        assertNotNull(created, "Plat non créé");
        double expected = Double.parseDouble(priceStr);
        assertEquals(expected, created.getPrice(), 1e-6);
    }

    @Then("les toppings finaux contiennent {string}")
    public void lesToppingsFinauxContiennent(String toppingName) {
        assertNotNull(created, "Plat non créé");
        assertTrue(
                created.getAvailableToppings().stream().anyMatch(t -> t.getName().equalsIgnoreCase(toppingName)),
                "Topping manquant: " + toppingName
        );
    }

    @Then("la liste des toppings finaux contient une seule fois {string}")
    public void laListeDesToppingsFinauxContientUneSeuleFois(String toppingName) {
        assertNotNull(created, "Plat non créé");
        long count = created.getAvailableToppings().stream()
                .filter(t -> t.getName().equalsIgnoreCase(toppingName))
                .count();
        assertEquals(1, count, "Le topping " + toppingName + " devrait apparaître une seule fois");
    }

    @Then("le restaurant contient exactement {int} plat")
    public void leRestaurantContientExactementPlat(int n) throws Exception {
        Restaurant restaurant = manager.getRestaurantFromRepo(currentRestaurantName);
        assertNotNull(restaurant, "Le restaurant n'a pas été trouvé sur le serveur");
        assertEquals(n, restaurant.getDishes().size());
    }


    @Then("une erreur {string} est levée")
    public void uneErreurMessageEstLevee(String messagePart) {
        assertNotNull(caught, "Aucune erreur levée");
        String msg = caught.getMessage();
        if (caught.getCause() != null) {
            msg += " " + caught.getCause().getMessage();
        }
        assertTrue(msg != null && msg.contains(messagePart),
                "Message attendu à contenir: " + messagePart + " mais était: " + msg);
    }

    @Then("une erreur de type {string} est levée")
    public void uneErreurDeTypeEstLevee(String typeName) {
        assertNotNull(caught, "Aucune erreur levée");

        if (typeName.equals("DishInDishes")) {
            assertInstanceOf(DishAlreadyInDishes.class, caught, "Erreur attendue DishInDishes, mais: " + caught.getClass());
        } else {
            fail("Type d'erreur non géré dans les steps: " + typeName);
        }
    }
}