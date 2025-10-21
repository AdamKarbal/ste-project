package Restaurant;

import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Exception.ScheduleNoExist;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestaurantScheduleManager {
    public static List<RestaurantSchedule> getTodaySchedules(Restaurant restaurant) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null)
            return List.of();

        LocalTime now = LocalTime.now();
        DayOfTheWeek today = convertDayOfWeek(LocalDate.now().getDayOfWeek());

        return restaurant.getRestaurantSchedules().stream()
                .filter(s -> s.getDay() == today)
                .filter(s -> s.getEnd().isAfter(now))
                .filter(s -> s.getCapacity() > 0)
                .collect(Collectors.toList());
    }

    public static Optional<RestaurantSchedule> getAvailableScheduleFor(Restaurant restaurant, LocalTime requestedTime) { // important : changer l'utilisation de requestedTime.isBefore()
        if (restaurant == null || restaurant.getRestaurantSchedules() == null || requestedTime == null)
            return Optional.empty();

        LocalDateTime now = LocalDateTime.now();

        /*if (requestedTime.isBefore(now.toLocalTime().plusMinutes(15))) {
            return Optional.empty();
        }*/

        DayOfTheWeek today = convertDayOfWeek(LocalDate.now().getDayOfWeek());

        return restaurant.getRestaurantSchedules().stream()
                .filter(s -> s.getDay() == today)
                .filter(s -> !requestedTime.isBefore(s.getStart()) && requestedTime.isBefore(s.getEnd()))
                .filter(s -> s.getCapacity() > 0)
                .findFirst();
    }


    public static boolean addSchedule(Restaurant restaurant, RestaurantSchedule newSchedule) {
        if (restaurant == null || newSchedule == null) return false;
        if (restaurant.getRestaurantSchedules() == null) return false;

        List<RestaurantSchedule> sameDaySchedules = restaurant.getRestaurantSchedules().stream()
                .filter(s -> s.getDay() == newSchedule.getDay())
                .toList();

        for (RestaurantSchedule existing : sameDaySchedules) {
            LocalTime start1 = existing.getStart();
            LocalTime end1 = existing.getEnd();
            LocalTime start2 = newSchedule.getStart();
            LocalTime end2 = newSchedule.getEnd();

            boolean overlap = start2.isBefore(end1) && end2.isAfter(start1);
            if (overlap) return false;
        }

        restaurant.addSchedule(newSchedule);
        return true;
    }

    public static boolean removeScheduleById(Restaurant restaurant, String id) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        return restaurant.getRestaurantSchedules().removeIf(s -> s.getId().equals(id));
    }

    public static int getCapacityById(Restaurant restaurant, String id) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) throw new RuntimeException("t'es une merde le s");
        for (RestaurantSchedule s : restaurant.getRestaurantSchedules()) {
            if (s.getId().equals(id)) return s.getCapacity();
        }
        throw new ScheduleNoExist("ce schedule n'existe pas");
    }

    public static boolean checkCapacity(Restaurant restaurant,String id){
        return getCapacityById(restaurant,id)>0;
    }

    public static boolean updateCapacityById(Restaurant restaurant, String id, int capacity) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        for (RestaurantSchedule s : restaurant.getRestaurantSchedules()) {
            if (s.getId().equals(id)) {
                s.setCapacity(capacity);
                return true;
            }
        }
        return false;
    }

    public static boolean incrementCapacityById(Restaurant restaurant, String id, int capacity) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        for (RestaurantSchedule s : restaurant.getRestaurantSchedules()) {
            if (s.getId().equals(id)) {
                s.addCapacity(capacity);
                return true;
            }
        }
        return false;
    }

    public static boolean isOpenNow(Restaurant restaurant) {
        return isOpenAt(restaurant, LocalDateTime.now());
    }

    public static boolean isOpenAt(Restaurant restaurant, LocalDateTime dateTime) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;

        var schedules = restaurant.getRestaurantSchedules();
        if (schedules.isEmpty()) return false;

        DayOfTheWeek today = convertDayOfWeek(dateTime.getDayOfWeek());
        LocalTime time = dateTime.toLocalTime();

        return schedules.stream().anyMatch(s ->
                s.getDay() == today &&
                        !time.isBefore(s.getStart()) &&
                        time.isBefore(s.getEnd())
        );
    }

    public static DayOfTheWeek convertDayOfWeek(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> DayOfTheWeek.MONDAY;
            case TUESDAY -> DayOfTheWeek.TUESDAY;
            case WEDNESDAY -> DayOfTheWeek.WEDNESDAY;
            case THURSDAY -> DayOfTheWeek.THURSDAY;
            case FRIDAY -> DayOfTheWeek.FRIDAY;
            case SATURDAY -> DayOfTheWeek.SATURDAY;
            case SUNDAY -> DayOfTheWeek.SUNDAY;
        };
    }
}
