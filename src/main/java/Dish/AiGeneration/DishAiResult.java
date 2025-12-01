package Dish.AiGeneration;

import Dish.Dish;
import java.util.ArrayList;
import java.util.List;

public class DishAiResult {

    private String status;                  // OK, LOW_CONFIDENCE, FORBIDDEN, NO_MATCH
    private String message;
    private double confidence;              // score calculé par TON back
    private boolean lexicalOk;
    private List<String> forbiddenWordsFound;
    private Dish dish;                      // Dish candidat NON sauvegardé

    public DishAiResult() {
        this.forbiddenWordsFound = new ArrayList<>();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public boolean isLexicalOk() { return lexicalOk; }
    public void setLexicalOk(boolean lexicalOk) { this.lexicalOk = lexicalOk; }

    public List<String> getForbiddenWordsFound() { return forbiddenWordsFound; }
    public void setForbiddenWordsFound(List<String> list) { this.forbiddenWordsFound = list; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }
}
