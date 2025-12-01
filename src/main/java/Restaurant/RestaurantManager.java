package Restaurant;
import Api.Utils.Requester;
import Dish.*;
import Dish.suggest.Suggestion;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.*;

public class RestaurantManager {
    private final SmartDishFactory smartDishFactory = new SmartDishFactory();

    public RestaurantManager(){}

    public void addDish(Dish d, String name) throws DishAlreadyInDishes {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if(restaurant.alreadyDish(d)) throw new DishAlreadyInDishes("Le plat "+ d+ " est déjà dans le restaurant." ) ;
        restaurant.addDish(d);
        System.out.println(d);
        Requester.createDish(d.toCreateDTO());
        saveRestaurant(restaurant);
    }

    public void updateDish(Dish old, Dish replacement,String name) throws dishNoInDishes {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if(!(restaurant.alreadyDish(old))) throw new dishNoInDishes("Impossible de mettre à jour le plat, il n'est pas présent.");
        restaurant.removeDish(old);
        restaurant.addDish(replacement);
        saveRestaurant(restaurant);
    }

    public void removeDish(Dish d, String name) throws dishNoInDishes{
        Restaurant restaurant = getRestaurantFromRepo(name);
        if(!(restaurant.alreadyDish(d))) throw new dishNoInDishes("Impossible d'enlever le plat :" + d + ", il n'est pas présent dans le restaurant.");
        restaurant.removeDish(d);
        saveRestaurant(restaurant);
    }

    public void addCuisineType(CuisineType c, String name) throws CuisineTypeInList {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if (restaurant.alreadyCuisineType(c))
            throw new CuisineTypeInList("Le type de cuisine " + c + " est déjà ajouté.");
        restaurant.addCuisineType(c);
        saveRestaurant(restaurant);
    }

    public void removeCuisineType(CuisineType c,String name) throws CuisineTypeNoExist {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if (!(restaurant.alreadyCuisineType(c)))
            throw new CuisineTypeNoExist("Le type de cuisine " + c + " n'existe pas dans le restaurant.");
        restaurant.removeCuisineType(c);
        saveRestaurant(restaurant);
    }


    public void setEstablishmentType(establishmentType e, String name) {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if (restaurant.getEstablishmentType() != null && restaurant.getEstablishmentType() != establishmentType.DEFAULT)
            throw new EstablishmentTypeAlreadySet("Le restaurant a déjà un type d'établissement défini.");
        restaurant.setEstablishmentType(e);
        saveRestaurant(restaurant);
    }

    public void removeEstablishmentType(String name) throws EstablishmentTypeNoExist {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if (restaurant.getEstablishmentType() == null)
            throw new EstablishmentTypeNoExist("Le restaurant n'a pas de type d'établissement défini.");
        restaurant.setEstablishmentType(null);
        saveRestaurant(restaurant);
    }

    public DishTopping createToppingForDish(Dish dish, String name, float price) {
        DishTopping topping = new DishTopping(name, price);
        dish.addAvailableTopping(topping);
        return topping;
    }

    public Suggestion previewSuggestion(String nameOrKeyword) {
        return smartDishFactory.suggest(nameOrKeyword);
    }

    public Dish createDish(DishInput input, String restoName) throws DishAlreadyInDishes {
        Dish dish = smartDishFactory.create(input);
        this.addDish(dish, restoName);

        return dish;
    }

    public Restaurant getRestaurantFromRepo(String name) {
        Restaurant r = Requester.getRestaurantByName(name);
        if (r == null) {
            throw new RuntimeException("Restaurant " + name + " introuvable dans le RestaurantRepo");
        }
        return r;
    }


    public void saveRestaurant(Restaurant restaurant) {
        Requester.updateRestaurant(restaurant.getName(), restaurant);
    }
}
