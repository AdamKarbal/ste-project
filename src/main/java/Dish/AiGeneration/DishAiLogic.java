package Dish.AiGeneration;

import Dish.Dish;
import Dish.DishTag;
import Dish.DishType;
import Dish.DishCategory;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class DishAiLogic {

    private static final double SCORE_THRESHOLD = 0.70;

    private static final Map<String,List<String>> FORBIDDEN = new HashMap<>();
    static {
        FORBIDDEN.put("italian", Arrays.asList("sushi", "burger"));
        FORBIDDEN.put("default", Arrays.asList("rat", "merde", "cul", "zizi"));
    }

    private static final Map<DishType, List<String>> TYPE_KEYWORDS = new HashMap<>();
    static {
        TYPE_KEYWORDS.put(DishType.PASTA, Arrays.asList("pate", "pasta", "spaghetti", "penne", "tagliatelle", "ravioli", "bolo"));
        TYPE_KEYWORDS.put(DishType.PIZZA, Arrays.asList("pizza", "margherita", "pepperoni"));
        TYPE_KEYWORDS.put(DishType.SALAD, Arrays.asList("salade", "salad"));
        TYPE_KEYWORDS.put(DishType.SOUP, Arrays.asList("soupe", "soup"));
    }

    public static DishAiResult process(DishAiRequest request,
                                       List<Map<String,String>> suggestions) {

        DishAiResult finalRes = new DishAiResult();
        double bestScore = 0;
        Dish bestDish = null;
        boolean lexicalOk = true;
        List<String> forbiddenWords = new ArrayList<>();

        String userName = request.getName();

        for (Map<String, String> sug : suggestions) {

            String label = sug.get("label");
            String desc  = sug.get("description");

            String combined = (label + " " + desc).toLowerCase(Locale.ROOT);

            // 1. Mots interdits
            List<String> forb = detectForbidden(request.getDomain(), combined);
            if (!forb.isEmpty()) {
                lexicalOk = false;
                forbiddenWords.addAll(forb);
                continue;
            }

            // 2. Similarité nom-user <-> label-IA
            double nameSimilarity = computeSimilarity(userName, label);

            // 3. Déduction type
            DishType type = guessType(combined);
            double typeScore = (type == null) ? 0.0 : computeTypePresence(combined, type);

            // 4. Pertinence description (keywords simples)
            double descRelevance = computeDescRelevance(combined);

            // 5. Originalité nom
            double nameQuality = computeNameQuality(label);

            // SCORE FINAL
            double score =
                    0.40 * nameSimilarity +
                            0.25 * typeScore +
                            0.20 * descRelevance +
                            0.15 * nameQuality;

            if (score > bestScore) {
                bestScore = score;
                bestDish = new Dish(
                        0.0,
                        label,
                        desc,
                        null,
                        type,
                        new ArrayList<DishTag>()
                );
            }
        }

        if (!lexicalOk) {
            finalRes.setStatus("FORBIDDEN");
            finalRes.setLexicalOk(false);
            finalRes.setForbiddenWordsFound(forbiddenWords);
            finalRes.setConfidence(0);
            finalRes.setMessage("Mots interdits trouvés.");
            return finalRes;
        }

        if (bestDish == null) {
            finalRes.setStatus("NO_MATCH");
            finalRes.setConfidence(0);
            finalRes.setMessage("Aucune suggestion valide.");
            return finalRes;
        }

        finalRes.setLexicalOk(true);
        finalRes.setForbiddenWordsFound(new ArrayList<>());
        finalRes.setDish(bestDish);
        finalRes.setConfidence(bestScore);

        if (bestScore < SCORE_THRESHOLD) {
            finalRes.setStatus("LOW_CONFIDENCE");
            finalRes.setMessage("Fiabilité trop faible (" + bestScore + ")");
            return finalRes;
        }

        finalRes.setStatus("OK");
        finalRes.setMessage("Suggestion sélectionnée.");
        return finalRes;
    }

    // ----------- TOOLS ------------

    private static List<String> detectForbidden(String domain, String text) {
        List<String> rules = FORBIDDEN.getOrDefault(domain, FORBIDDEN.get("default"));
        return rules.stream().filter(text::contains).collect(Collectors.toList());
    }

    private static double computeSimilarity(String a, String b) {
        if (a == null || b == null) return 0;

        String na = normalize(a);
        String nb = normalize(b);

        String[] wa = na.split(" ");
        String[] wb = nb.split(" ");

        long matches = Arrays.stream(wa).filter(w -> Arrays.asList(wb).contains(w)).count();

        return (double) matches / wa.length;
    }

    private static String normalize(String s) {
        return Normalizer.normalize(s.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replaceAll("[^a-z ]","").trim();
    }

    private static DishType guessType(String text) {
        for (var e : TYPE_KEYWORDS.entrySet()) {
            for (String kw: e.getValue())
                if (text.contains(kw)) return e.getKey();
        }
        return null;
    }

    private static double computeTypePresence(String text, DishType type) {
        List<String> kws = TYPE_KEYWORDS.get(type);
        long hits = kws.stream().filter(text::contains).count();
        return (double)hits / kws.size();
    }

    private static double computeDescRelevance(String text) {
        int score = 0;
        if (text.contains("tomate")) score++;
        if (text.contains("pate")) score++;
        if (text.contains("ital")) score++;
        return score / 3.0;
    }

    private static double computeNameQuality(String label) {
        // Simple: long = mieux, mots premium
        double score = 0.0;
        if (label.length() > 15) score += 0.4;
        if (label.matches(".*chef.*")) score += 0.3;
        if (label.matches(".*maison.*")) score += 0.3;
        if (label.matches(".*authentique.*")) score += 0.3;
        return Math.min(1.0, score);
    }
}
