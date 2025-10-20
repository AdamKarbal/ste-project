package Dish.suggest;

import Dish.*;
import java.text.Normalizer;
import java.util.*;

/**
 * Suggester simple et fiable basé sur mots-clés + templates.
 * - Normalise l'entrée (accents, casse, espaces).
 * - Gère des alias (ex: "margherita" → "pizza").
 * - Retourne une Suggestion "raisonnable" ou une Suggestion vide si rien.
 */
public class KeywordSuggester implements DishSuggestionEngine {

    private final Map<String, Suggestion> index = new HashMap<>();     // clé canonique -> template
    private final Map<String, String> aliasToKey = new HashMap<>();    // alias -> clé canonique

    public KeywordSuggester() {
        // TEMPLATES
        addTemplate("couscous", make(
                        DishCategory.MAIN_COURSE, DishType.MEAT, 12.5,
                        "Semoule, légumes, viande.",
                        List.of(DishTag.HALAL),
                        List.of(new DishTopping("Pois chiches", 0.8f),
                                new DishTopping("Légumes", 1.0f))),
                "couscous royal", "couscous agneau"
        );

        addTemplate("pizza", make(
                        DishCategory.MAIN_COURSE, DishType.PIZZA, 10.0,
                        "Base tomate, mozzarella, basilic.",
                        List.of(DishTag.VEGETARIAN),
                        List.of(new DishTopping("Mozzarella", 1.2f),
                                new DishTopping("Basilic", 0.5f))),
                "margherita", "4 fromages", "quatre fromages", "pepperoni"
        );

        addTemplate("burger", make(
                        DishCategory.MAIN_COURSE, DishType.MEAT, 11.0,
                        "Pain, steak, cheddar, condiments.",
                        List.of(),
                        List.of(new DishTopping("Cheddar", 1.0f),
                                new DishTopping("Cornichons", 0.5f))),
                "cheeseburger", "bacon burger"
        );

        addTemplate("tiramisu", make(
                        DishCategory.DESSERT, DishType.CAKE, 5.5,
                        "Dessert italien au café et mascarpone.",
                        List.of(),
                        List.of(new DishTopping("Cacao", 0.0f))),
                "tiramisu maison", "tiramisu classique"
        );

        addTemplate("sushi", make(
                        DishCategory.MAIN_COURSE, DishType.OTHER, 12.0,
                        "Assortiment de riz vinaigré et poisson.",
                        List.of(),
                        List.of(new DishTopping("Gingembre", 0.0f),
                                new DishTopping("Wasabi", 0.0f))),
                "maki", "nigiri", "california"
        );

        // ITALIEN
        addTemplate("pasta carbonara", make(
                        DishCategory.MAIN_COURSE, DishType.PASTA, 11.0,
                        "Spaghetti, pancetta, oeuf, pecorino.",
                        List.of(), List.of(new DishTopping("Pecorino", 1.0f), new DishTopping("Pancetta", 1.5f))),
                "carbonara", "spaghetti carbonara"
        );
        addTemplate("pasta bolognese", make(
                        DishCategory.MAIN_COURSE, DishType.PASTA, 11.0,
                        "Pâtes sauce tomate bœuf mijoté.",
                        List.of(), List.of(new DishTopping("Parmesan", 1.0f))),
                "bolognaise", "bolognese", "spaghetti bolognese"
        );
        addTemplate("risotto", make(
                        DishCategory.MAIN_COURSE, DishType.OTHER, 12.0,
                        "Riz crémeux, bouillon, parmesan.",
                        List.of(), List.of(new DishTopping("Parmesan", 1.0f), new DishTopping("Champignons", 1.2f))),
                "risotto funghi", "risotto champignons"
        );

        // ORIENTAL / MOYEN-ORIENT
        addTemplate("tajine", make(
                        DishCategory.MAIN_COURSE, DishType.MEAT, 13.0,
                        "Mijoté épicé, légumes, viande.",
                        List.of(DishTag.HALAL), List.of(new DishTopping("Abricots secs", 0.8f), new DishTopping("Amandes", 0.6f))),
                "tajine poulet", "tajine agneau"
        );
        addTemplate("falafel", make(
                        DishCategory.MAIN_COURSE, DishType.OTHER, 9.5,
                        "Boulettes pois chiches, herbes, sésame.",
                        List.of(DishTag.VEGETARIAN, DishTag.VEGAN), List.of(new DishTopping("Tahini", 0.5f), new DishTopping("Pickles", 0.5f))),
                "falafels"
        );
        addTemplate("kebab", make(
                        DishCategory.MAIN_COURSE, DishType.MEAT, 10.0,
                        "Viande grillée, pain, crudités, sauce.",
                        List.of(DishTag.HALAL), List.of(new DishTopping("Sauce blanche", 0.0f), new DishTopping("Harissa", 0.0f))),
                "shawarma", "doner", "durum"
        );

        // ASIE
        addTemplate("ramen", make(
                        DishCategory.MAIN_COURSE, DishType.SOUP, 12.0,
                        "Bouillon, nouilles, garnitures.",
                        List.of(), List.of(new DishTopping("Œuf mariné", 1.0f), new DishTopping("Algues nori", 0.5f))),
                "tonkotsu ramen", "shoyu ramen", "miso ramen"
        );
        addTemplate("pad thai", make(
                        DishCategory.MAIN_COURSE, DishType.OTHER, 11.5,
                        "Nouilles sautées, tamarin, cacahuètes.",
                        List.of(), List.of(new DishTopping("Crevettes", 1.5f), new DishTopping("Tofu", 1.0f))),
                "pad thaï", "padthai"
        );
        addTemplate("curry", make(
                        DishCategory.MAIN_COURSE, DishType.OTHER, 12.0,
                        "Sauce épicée, lait de coco, riz.",
                        List.of(DishTag.SPICY), List.of(new DishTopping("Poulet", 2.0f), new DishTopping("Légumes", 1.0f))),
                "curry poulet", "curry veggie", "green curry", "red curry"
        );

        // TEX-MEX
        addTemplate("tacos", make(
                        DishCategory.MAIN_COURSE, DishType.OTHER, 9.0,
                        "Tortillas garnies, salsa.",
                        List.of(), List.of(new DishTopping("Guacamole", 1.2f), new DishTopping("Fromage", 1.0f))),
                "taco", "tacos boeuf", "tacos poulet"
        );
        addTemplate("burrito", make(
                        DishCategory.MAIN_COURSE, DishType.OTHER, 10.5,
                        "Tortilla roulée, riz, haricots, garnitures.",
                        List.of(), List.of(new DishTopping("Crème sûre", 0.8f), new DishTopping("Piments jalapeño", 0.6f))),
                "buritos", "burritos"
        );
        addTemplate("quesadilla", make(
                DishCategory.MAIN_COURSE, DishType.OTHER, 9.5,
                "Tortilla grillée, fromage fondu.",
                List.of(), List.of(new DishTopping("Poulet", 1.5f), new DishTopping("Poivrons", 0.7f)))
        );

        // SALADES & SOUPES
        addTemplate("salade caesar", make(
                        DishCategory.MAIN_COURSE, DishType.SALAD, 9.5,
                        "Laitue, croûtons, parmesan, sauce Caesar.",
                        List.of(), List.of(new DishTopping("Poulet", 1.8f), new DishTopping("Bacon", 1.2f))),
                "caesar", "caesar salad"
        );
        addTemplate("salade nicoise", make(
                        DishCategory.MAIN_COURSE, DishType.SALAD, 10.0,
                        "Thon, œufs, olives, haricots verts.",
                        List.of(), List.of(new DishTopping("Anchois", 1.0f))),
                "niçoise", "nicoise"
        );
        addTemplate("minestrone", make(
                DishCategory.MAIN_COURSE, DishType.SOUP, 8.5,
                "Soupe de légumes, haricots, pâtes.",
                List.of(), List.of(new DishTopping("Parmesan", 0.8f)))
        );

        // DESSERTS
        addTemplate("cheesecake", make(
                        DishCategory.DESSERT, DishType.CAKE, 5.9,
                        "Gâteau fromage frais, biscuit.",
                        List.of(), List.of(new DishTopping("Coulis fruits rouges", 0.8f))),
                "cheese cake"
        );
        addTemplate("brownie", make(
                DishCategory.DESSERT, DishType.CAKE, 4.8,
                "Gâteau chocolat fondant.",
                List.of(), List.of(new DishTopping("Noix", 0.7f)))
        );
        addTemplate("creme brulee", make(
                        DishCategory.DESSERT, DishType.OTHER, 5.0,
                        "Crème à la vanille, caramel croquant.",
                        List.of(), List.of()),
                "crème brûlée"
        );
    }

    @Override
    public Suggestion suggest(String nameOrKeyword) {
        String key = norm(nameOrKeyword);
        if (key.isEmpty()) return new Suggestion();

        // 1) alias exact (ex: "margherita" -> "pizza")
        String canonical = aliasToKey.get(key);
        if (canonical != null) return cloneSuggestion(index.get(canonical));

        // 2) match partiel sur clé canonique (ex: "pizza margherita")
        for (var e : index.entrySet()) {
            if (key.contains(e.getKey())) return cloneSuggestion(e.getValue());
        }

        // 3) match partiel sur alias (ex: phrase contenant "margherita")
        for (var e : aliasToKey.entrySet()) {
            if (key.contains(e.getKey())) return cloneSuggestion(index.get(e.getValue()));
        }

        return new Suggestion(); // aucune correspondance
    }

    // ---------- Helpers ----------

    private void addTemplate(String key, Suggestion s, String... aliases) {
        index.put(norm(key), s);
        for (String a : aliases) {
            aliasToKey.put(norm(a), norm(key));
        }
    }

    private Suggestion make(DishCategory cat, DishType type, double price,
                            String desc, List<DishTag> tags, List<DishTopping> tops) {
        Suggestion s = new Suggestion();
        s.category = cat;
        s.type = type;
        s.suggestedPrice = price;
        s.description = desc;
        s.tags = tags;
        s.toppings = tops;
        return s;
    }

    // Renvoie une copie légère pour éviter de muter le template partagé
    private Suggestion cloneSuggestion(Suggestion src) {
        Suggestion s = new Suggestion();
        s.description = src.description;
        s.category = src.category;
        s.type = src.type;
        s.suggestedPrice = src.suggestedPrice;
        s.tags = (src.tags == null) ? null : new ArrayList<>(src.tags);
        s.toppings = (src.toppings == null) ? null : new ArrayList<>(src.toppings);
        return s;
    }

    // Normalisation (accents/casse/espaces)
    private String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }
}
