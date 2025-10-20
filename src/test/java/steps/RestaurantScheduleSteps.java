package steps;

import Utils.InvalidDate;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import org.junit.jupiter.api.Assertions;

import Account.AccountManager;
import Dish.Dish;
import Dish.DishTag;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.cuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;
import Restaurant.RestaurantManager;
import Restaurant.RestaurantSchedule;

import java.time.LocalTime;
import java.util.*;


public class RestaurantScheduleSteps {
    private RestaurantRepo repo;
    private RestaurantService service;
    private Map<String, AccountManager> restaurateurs = new HashMap<>();
    private RestaurantManager currentManager;
    private Restaurant currentRestaurant;

    @Before
    public void setup() {
        repo = new RestaurantRepo(new ArrayList<>());
        service = new RestaurantService(repo);
    }

    @Given("un catalogue de restos")
    public void un_catalogue_de_restos(DataTable table) {
        for(Map<String,String> row : table.asMaps()){
            String name = row.get("name");
            List<cuisineType> c = List.of(cuisineType.valueOf(row.get("cuisineType").toUpperCase()));
            establishmentType e = establishmentType.valueOf(row.get("establishmentType").toUpperCase());
            List<DishTag> t1 = parseTags(row.get("tags1"));
            List<DishTag> t2 = parseTags(row.get("tags2"));
            Dish d1 = new Dish(Double.parseDouble(row.get("price1")),row.get("dish1"),null,null,null,null);
            Dish d2 = new Dish(Double.parseDouble(row.get("price2")),row.get("dish2"),null,null,null,null);
            d1.setDishTag(new ArrayList<>(t1));
            d2.setDishTag(new ArrayList<>(t2));
            List<Dish> dishes = List.of(d1,d2);
            List<RestaurantSchedule> schedules = new ArrayList<>();
            schedules.add(makeSchedule(DayOfTheWeek.MONDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.MONDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.TUESDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.TUESDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.WEDNESDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.WEDNESDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.THURSDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.THURSDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.FRIDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.FRIDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.SATURDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.SUNDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.SUNDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            Restaurant r = new Restaurant.Builder()
                    .name(name)
                    .dishes(dishes)
                    .restaurantSchedules(schedules)
                    .cuisineType(c)
                    .establishmentType(e)
                    .build();
            service.addRestaurant(r);
        }
    }




    // Méthodes utilitaires
    private static RestaurantSchedule makeSchedule(DayOfTheWeek day, String start, String end, String capStr) {
        if (start == null || end == null || start.isBlank() || end.isBlank()) {
            // pas de créneau fourni : on met un créneau neutre capacité 0
            return new RestaurantSchedule(day, hhmm("00:00"), hhmm("00:00"), 0);
        }
        int cap = (capStr == null || capStr.isBlank()) ? 0 : Integer.parseInt(capStr.trim());
        return new RestaurantSchedule(day, hhmm(start.trim()), hhmm(end.trim()), cap);
    }

    private static LocalTime hhmm(String s) {
        String[] p = s.split(":");
        int h = Integer.parseInt(p[0].trim());
        int m = Integer.parseInt(p[1].trim());
        try {
            return LocalTime.of(h, m);
        } catch (InvalidDate e) {
            throw new RuntimeException("Heure invalide: " + s, e);
        }
    }

    private static List<DishTag> parseTags(String cell) {
        if (cell == null || cell.isBlank()) return List.of();
        return Arrays.stream(cell.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(RestaurantScheduleSteps::toTag)
                .toList();
    }

    private static DishTag toTag(String raw) {
        // essai direct par nom d'enum (VEGAN, GLUTEN_FREE, ...)
        String candidate = raw.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try { return DishTag.valueOf(candidate); } catch (IllegalArgumentException ignore) {}

        // essai par label ("Vegan", "Gluten-free", ...) en normalisant
        String norm = java.text.Normalizer.normalize(raw.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toUpperCase().replace(' ', '_').replace('-', '_');
        for (DishTag t : DishTag.values()) {
            String lab = java.text.Normalizer.normalize(t.getLabel(), java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}+", "").toUpperCase().replace(' ', '_').replace('-', '_');
            if (lab.equals(norm)) return t;
        }
        throw new IllegalArgumentException("Tag inconnu: " + raw);
    }
}
