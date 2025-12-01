package Dish.AiGeneration;

public class DishAiRequest {
    private String name;
    private String image;   // optionnel, peut être null
    private String domain;  // ex : "italian" (optionnel)

    public DishAiRequest(String name, String image, String domain) {
        this.name = name;
        this.image = image;
        this.domain = domain;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}
