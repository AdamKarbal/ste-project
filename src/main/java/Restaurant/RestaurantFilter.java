package Restaurant;

import Api.Utils.Requester;
import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class RestaurantFilter {
    RestaurantRepo restaurantRepo;
    public RestaurantFilter(RestaurantRepo restaurantRepo) {
        this.restaurantRepo = restaurantRepo;
    }

    public void updateRepo(RestaurantRepo restaurantRepo) {
        this.restaurantRepo = restaurantRepo;
    }

    public List<Restaurant> filterByType(establishmentType type) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant -> restaurant.getEstablishmentType().equals(type))
                .toList() ;
    }

    public List<Restaurant> filterByAvailability() {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(r -> Requester.isOpenNow(r.getName()))
                .toList();
    }

    public List<Restaurant> filterByAvailability(LocalTime targetTime) {
        LocalDateTime targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime);

        return restaurantRepo.getRestaurants()
                .stream()
                .filter(r -> Requester.isOpenAt(r.getName(), targetDateTime))
                .toList();
    }

    public List<Restaurant> findName(String name) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant -> restaurant.getName().toLowerCase().contains(name.toLowerCase()))
                .toList() ;
    }

    public List<Restaurant> filterByCuisineType(CuisineType type) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant -> restaurant.getCuisineTypes().stream().anyMatch(cuisineType -> cuisineType.equals(type)))
                .toList() ;
    }

    public List<Restaurant> filterByTag(DishTag tag) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(r -> r.getDishes() != null &&
                        r.getDishes().stream()
                                .flatMap(dish -> {
                                    var tags = dish.getDishTag();
                                    return tags == null ? Stream.<DishTag>empty() : tags.stream();
                                })
                                .anyMatch(t -> t.equals(tag))
                )
                .toList();
    }

    public List<Restaurant> filterByAllTags(List<DishTag> tags) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(r -> r.getDishes() != null &&
                        r.getDishes().stream()
                                .anyMatch(dish -> {
                                    var dishTags = dish.getDishTag();
                                    return dishTags != null && dishTags.containsAll(tags);
                                })
                )
                .toList();
    }

    public List<Restaurant> filterByPriceMin(double minPrice) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .anyMatch(dish -> dish.getPrice() >= minPrice)
                )
                .toList();
    }

    public List<Restaurant> filterByPriceMax(double maxPrice) {
        System.out.println("=== DEBUG Prix max " + maxPrice + " ===");
        System.out.println("=== DEBUG nb restaurants : "+Requester.getAllRestaurants().size());
        restaurantRepo.getRestaurants().forEach(r -> System.out.println(
                r.getName() + " : " +
                        r.getDishes().stream().map(d -> d.getName() + "=" + d.getPrice()).toList()
        ));

        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .anyMatch(dish -> dish.getPrice() <= maxPrice)
                )
                .toList();
    }


    public List<Restaurant> filterByPriceRange(double minPrice, double maxPrice) {
        System.out.println("=== DEBUG Prix max " + maxPrice + " ===");
        System.out.println("=== DEBUG nb restaurants : "+Requester.getAllRestaurants().size());
        restaurantRepo.getRestaurants().forEach(r -> System.out.println(
                r.getName() + " : " +
                        r.getDishes().stream().map(d -> d.getName() + "=" + d.getPrice()).toList()
        ));
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .anyMatch(dish ->
                                        dish.getPrice() >= minPrice && dish.getPrice() <= maxPrice
                                )
                )
                .toList();
    }


    public List<Restaurant> filter(FilterCriteria c) {
        List<Restaurant> acc = new ArrayList<>(restaurantRepo.getRestaurants());
        if (c == null) return acc;

        // 1) Nom
        if (c.getNameContains() != null && !c.getNameContains().isBlank()) {
            List<Restaurant> byName = this.findName(c.getNameContains());
            acc = intersectOrdered(acc, byName);
        }

        // --- Types de cuisine multiples ---
        if (c.getCuisines() != null && !c.getCuisines().isEmpty()) {
            List<Restaurant> byCuisine = new ArrayList<>();
            for (CuisineType ct : c.getCuisines()) {
                byCuisine.addAll(this.filterByCuisineType(ct));
            }
            acc = intersectOrdered(acc, byCuisine);
        }

// --- Types d’établissement multiples ---
        if (c.getEstablishments() != null && !c.getEstablishments().isEmpty()) {
            List<Restaurant> byType = new ArrayList<>();
            for (establishmentType et : c.getEstablishments()) {
                byType.addAll(this.filterByType(et));
            }
            acc = intersectOrdered(acc, byType);
        }


        // 4) Disponibilité
        if (c.getOpenAt() != null) {
            List<Restaurant> openAt = this.filterByAvailability(c.getOpenAt());
            acc = intersectOrdered(acc, openAt);
        } else if (Boolean.TRUE.equals(c.getOpenNow())) {
            List<Restaurant> openNow = this.filterByAvailability();
            acc = intersectOrdered(acc, openNow);
        }

        // 5) Prix
        if (c.getPriceMin() != null && c.getPriceMax() != null) {
            List<Restaurant> byRange = this.filterByPriceRange(c.getPriceMin(), c.getPriceMax());
            acc = intersectOrdered(acc, byRange);
        } else {
            if (c.getPriceMin() != null) {
                List<Restaurant> byMin = this.filterByPriceMin(c.getPriceMin());
                acc = intersectOrdered(acc, byMin);
            }
            if (c.getPriceMax() != null) {
                List<Restaurant> byMax = this.filterByPriceMax(c.getPriceMax());
                acc = intersectOrdered(acc, byMax);
            }
        }

        // 6) Tags
        if (c.getTagsAnyOf() != null && !c.getTagsAnyOf().isEmpty()) {
            List<Restaurant> union = new ArrayList<>();
            for (DishTag t : c.getTagsAnyOf()) {
                List<Restaurant> withTag = this.filterByTag(t);
                addAllUnique(union, withTag);
            }
            acc = intersectOrdered(acc, union);
        }

        if (c.getTagsAllOf() != null && !c.getTagsAllOf().isEmpty()) {
            List<Restaurant> allTags = this.filterByAllTags(new ArrayList<>(c.getTagsAllOf()));
            acc = intersectOrdered(acc, allTags);
        }

        return acc;
    }

    /* --- Helpers privés, conservant l’ordre initial et évitant les doublons --- */

    // Intersection en gardant l’ordre de la 1re liste
    private static <T> List<T> intersectOrdered(List<T> base, List<T> other) {
        if (base.isEmpty() || other.isEmpty()) return Collections.emptyList();
        List<T> res = new ArrayList<>();
        for (T x : base) if (other.contains(x)) res.add(x);
        return res;
    }

    // Ajoute src à dest sans doublon (ordre de 1ère apparition)
    private static <T> void addAllUnique(List<T> dest, List<T> src) {
        for (T x : src) if (!dest.contains(x)) dest.add(x);
    }


}
