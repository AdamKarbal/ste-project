package Dish.AiGeneration;

import Dish.Dish;
import Dish.DishType;

import java.text.Normalizer;
import java.util.*;

public class DishAiLogic {

    private static final double SCORE_THRESHOLD = 0.70;

    private static final Map<String, List<String>> FORBIDDEN = new HashMap<>();
    static {
        FORBIDDEN.put("default", Arrays.asList(
                "rat", "merde", "zizi", "cul",
                "putain", "salope",
                "fion", "anus",
                "caca", "vomi", "dégueu", "dégueulasse",
                "diarrhée",
                "penis", "shit",
                "bitch", "fuck"
        ));
        List<String> italianForbidden = Arrays.asList(
                // Japonais
                "sushi", "maki", "ramen", "udon", "sashimi", "yakitori", "tempura",
                // Mexicain
                "taco", "tacos", "burrito", "quesadilla", "enchilada",
                // Indien
                "curry", "masala", "tikka", "biryani", "naan",
                // Turc / Kebab
                "kebab", "doner", "lahmacun",
                // Asiatique divers
                "pho", "bao", "bibimbap",
                // US
                "hot dog", "hotdog", "fried chicken"
        );
        FORBIDDEN.put("italian", italianForbidden);
        FORBIDDEN.put("italienne", italianForbidden);

        List<String> japaneseForbidden = Arrays.asList(
                "pizza", "pate", "pasta", "carbonara", "bolognaise", "bolo",
                "kebab", "taco", "tacos", "burrito",
                "couscous", "tajine"
        );
        FORBIDDEN.put("japanese", japaneseForbidden);
        FORBIDDEN.put("japonaise", japaneseForbidden);

        List<String> indianForbidden = Arrays.asList(
                "pizza", "pate", "pasta", "spaghetti",
                "sushi", "ramen", "maki"
        );
        FORBIDDEN.put("indian", indianForbidden);
        FORBIDDEN.put("indienne", indianForbidden);

        // Cuisine mexicaine
        List<String> mexicanForbidden = Arrays.asList(
                "sushi", "ramen", "maki",
                "pate", "pasta", "pizza",
                "kebab", "couscous", "tajine"
        );
        FORBIDDEN.put("mexican", mexicanForbidden);
        FORBIDDEN.put("mexicaine", mexicanForbidden);
    }

    private static final Map<DishType, List<String>> TYPE_KEYWORDS = new HashMap<>();
    static {
        TYPE_KEYWORDS.put(DishType.PASTA, Arrays.asList(
                "pate", "pasta", "spaghetti", "tagliatelle", "penne",
                "ravioli", "lasagne", "linguine", "fusilli", "macaroni",
                "carbonara", "bolognaise", "bolo"
        ));
        TYPE_KEYWORDS.put(DishType.MEAT, Arrays.asList(
                "viande", "steak", "poulet", "chicken", "beef", "boeuf",
                "agneau", "porc", "lamb", "meat", "rôti", "escalope",
                "filet", "côte", "grillade", "barbecue"
        ));
        TYPE_KEYWORDS.put(DishType.PIZZA, Arrays.asList(
                "pizza", "pepperoni", "margherita", "quatre fromages",
                "4 fromages", "napolitaine", "calzone"
        ));
        TYPE_KEYWORDS.put(DishType.ICE_CREAM, Arrays.asList(
                "glace", "ice cream", "gelato", "sorbet", "frozen"
        ));
        TYPE_KEYWORDS.put(DishType.CAKE, Arrays.asList(
                "cake", "gâteau", "gateau", "cheesecake",
                "fondant", "tarte", "brownie"
        ));
        TYPE_KEYWORDS.put(DishType.SALAD, Arrays.asList(
                "salade", "salad", "cesar", "caesar", "crudités"
        ));
        TYPE_KEYWORDS.put(DishType.SOUP, Arrays.asList(
                "soupe", "soup", "velouté", "broth", "bouillon", "ramen"
        ));
        TYPE_KEYWORDS.put(DishType.SEAFOOD, Arrays.asList(
                "fish", "poisson", "seafood", "crevette", "shrimp",
                "saumon", "thon", "moule", "huitre", "oyster", "calamar",
                "poulpe"
        ));
        TYPE_KEYWORDS.put(DishType.SANDWICH, Arrays.asList(
                "sandwich", "burger", "panini", "kebab", "tacos",
                "sub", "baguette", "toast"
        ));
        TYPE_KEYWORDS.put(DishType.OTHER, Arrays.asList(
                "dish", "food", "meal", "plat"
        ));
    }

    public static DishAiResult process(DishAiRequest request,
                                       List<Map<String,String>> suggestions) {

        DishAiResult finalRes = new DishAiResult();
        double bestScore = 0.0;
        Dish bestDish = null;
        boolean lexicalOk = true;
        List<String> forbiddenWords = new ArrayList<>();

        String userName = request.getName();

        for (Map<String, String> sug : suggestions) {

            String label = sug.get("label");
            String desc  = sug.get("description");

            if ((label == null || label.isBlank()) && (desc == null || desc.isBlank())) {
                continue;
            }

            String combined = ((label == null ? "" : label) + " " +
                    (desc  == null ? "" : desc))
                    .toLowerCase(Locale.ROOT);

            List<String> forb = detectForbidden(request.getDomain(), combined);
            if (!forb.isEmpty()) {
                lexicalOk = false;
                forbiddenWords.addAll(forb);
                continue;
            }

            double nameSimilarity = computeSimilarity(userName, label);

            DishType type = guessType(combined);
            double typeScore = (type == null) ? 0.0 : computeTypePresence(combined, type);

            double descRelevance = computeDescRelevance(combined);

            double nameQuality = computeNameQuality(label);

            double score = 0.60;

            // 1) Cohérence du type (PASTA, PIZZA, etc.)
            if (type != null) {
                if (typeScore >= 0.4) {
                    score += 0.20;
                } else if (typeScore > 0.0) {
                    score += 0.10 + 0.10 * typeScore;
                } else {
                    score += 0.05;
                }
            }

            // 2) Pertinence de la description (tomate/pates/italien)
            if (descRelevance >= 0.66) {
                score += 0.15;
            } else if (descRelevance >= 0.33) {
                score += 0.10;
            } else if (descRelevance > 0.0) {
                score += 0.05;
            }

            // 3) Similarité du nom utilisateur ↔ nom généré
            if (nameSimilarity >= 0.75) {
                score += 0.15;
            } else if (nameSimilarity >= 0.4) {
                score += 0.10;
            } else if (nameSimilarity > 0.0) {
                score += 0.05;
            }

            if (nameQuality >= 0.66) {
                score += 0.10;
            } else if (nameQuality >= 0.33) {
                score += 0.05;
            } else if (nameQuality > 0.0) {
                score += 0.02;
            }

            if (label != null && !label.isBlank() && desc != null && !desc.isBlank()) {
                score += 0.03;
            }

            score = Math.max(0.0, Math.min(1.0, score));

            if (score > bestScore) {
                bestScore = score;
                bestDish = new Dish(
                        0.0,
                        label,
                        desc,
                        null,          // category (à remplir plus tard si besoin)
                        type,
                        new ArrayList<>()
                );
            }
        }

        if (!lexicalOk) {
            finalRes.setStatus("FORBIDDEN");
            finalRes.setLexicalOk(false);
            finalRes.setForbiddenWordsFound(deduplicate(forbiddenWords));
            finalRes.setConfidence(0.0);
            finalRes.setMessage("Mots interdits trouvés dans les suggestions IA.");
            return finalRes;
        }

        if (bestDish == null) {
            finalRes.setStatus("NO_MATCH");
            finalRes.setConfidence(0.0);
            finalRes.setLexicalOk(true);
            finalRes.setForbiddenWordsFound(new ArrayList<>());
            finalRes.setMessage("Aucune suggestion IA exploitable.");
            return finalRes;
        }

        finalRes.setLexicalOk(true);
        finalRes.setForbiddenWordsFound(new ArrayList<>());
        finalRes.setDish(bestDish);
        finalRes.setConfidence(bestScore);

        if (bestScore < SCORE_THRESHOLD) {
            finalRes.setStatus("LOW_CONFIDENCE");
            finalRes.setMessage("Fiabilité jugée insuffisante (" + bestScore + ").");
            return finalRes;
        }

        finalRes.setStatus("OK");
        finalRes.setMessage("Suggestion IA sélectionnée avec succès.");
        return finalRes;
    }

    private static List<String> detectForbidden(String domain, String text) {
        if (text == null || text.isBlank()) return List.of();
        String lower = text.toLowerCase(Locale.ROOT);

        List<String> found = new ArrayList<>();

        // 1) Global (default)
        List<String> global = FORBIDDEN.getOrDefault("default", Collections.emptyList());
        for (String w : global) {
            if (lower.contains(w)) {
                found.add(w);
            }
        }

        // 2) Spécifique au "domain" (cuisine)
        if (domain != null) {
            String key = domain.toLowerCase(Locale.ROOT);
            List<String> specific = FORBIDDEN.getOrDefault(key, Collections.emptyList());
            for (String w : specific) {
                if (lower.contains(w)) {
                    found.add(w);
                }
            }
        }

        return deduplicate(found);
    }

    private static List<String> deduplicate(List<String> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }

    private static double computeSimilarity(String a, String b) {
        if (a == null || b == null) return 0;

        String na = normalize(a);
        String nb = normalize(b);

        String[] wa = na.split(" ");
        String[] wb = nb.split(" ");

        long matches = Arrays.stream(wa).filter(w -> Arrays.asList(wb).contains(w)).count();

        return (wa.length == 0) ? 0.0 : (double) matches / wa.length;
    }

    private static String normalize(String s) {
        return Normalizer.normalize(s.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replaceAll("[^a-z ]","").trim();
    }

    private static DishType guessType(String text) {
        if (text == null) return null;
        String lower = text.toLowerCase(Locale.ROOT);

        for (var e : TYPE_KEYWORDS.entrySet()) {
            for (String kw: e.getValue()) {
                if (lower.contains(kw)) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    private static double computeTypePresence(String text, DishType type) {
        if (text == null || type == null) return 0.0;
        String lower = text.toLowerCase(Locale.ROOT);
        List<String> kws = TYPE_KEYWORDS.get(type);
        if (kws == null || kws.isEmpty()) return 0.0;

        long hits = kws.stream().filter(lower::contains).count();
        return (double) hits / kws.size();
    }

    private static double computeDescRelevance(String text) {
        if (text == null) return 0.0;
        String lower = text.toLowerCase(Locale.ROOT);

        int score = 0;
        if (lower.contains("tomate")) score++;
        if (lower.contains("pate") || lower.contains("pasta")) score++;
        if (lower.contains("ital")) score++;
        return score / 3.0;
    }

    private static double computeNameQuality(String label) {
        if (label == null) return 0.0;
        String lower = label.toLowerCase(Locale.ROOT);

        double score = 0.0;
        if (label.length() > 15) score += 0.4;
        if (lower.contains("chef")) score += 0.3;
        if (lower.contains("maison")) score += 0.3;
        if (lower.contains("authentique")) score += 0.3;
        return Math.min(1.0, score);
    }
}