package Restaurant;

import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Exception.ScheduleNoExist;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
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

        /*LocalDateTime now = LocalDateTime.now();

        if (requestedTime.isBefore(now.toLocalTime().plusMinutes(15))) {
            return Optional.empty();
        }*/

        DayOfTheWeek today = convertDayOfWeek(LocalDate.now().getDayOfWeek());

        return restaurant.getRestaurantSchedules().stream()
                .filter(s -> s.getDay() == today)
                .filter(s -> !requestedTime.isBefore(s.getStart()) && requestedTime.isBefore(s.getEnd()))
                .filter(s -> s.getCapacity() > 0)
                .findFirst();
    }

    public static boolean addSchedule(Restaurant restaurant, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        if (start == null || end == null || day == null) return false;

        if (end.isAfter(start.plusMinutes(30))) {
            createIntervals(restaurant, day, start, end, capacity);
            return true;
        }

        if (!restaurant.alreadyScheduled(start, start.plusMinutes(30), day)) {
            restaurant.addSchedule(new RestaurantSchedule(day, start, capacity));
            return true;
        }

        return false;
    }


    private static void createIntervals(Restaurant restaurant, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) {
        LocalTime current = start;
        do {
            if (!restaurant.alreadyScheduled(current, current.plusMinutes(30), day)) {
                restaurant.addSchedule(new RestaurantSchedule(day, current, capacity));
            }

            current = current.plusMinutes(30);

        } while (!current.equals(LocalTime.MIDNIGHT) && current.isBefore(end));
    }


    public static void updateSchedule(Restaurant restaurant, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) {
        Objects.requireNonNull(day);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        List<RestaurantSchedule> schedules = restaurant.getRestaurantSchedules();
        if (schedules != null) {
            schedules.removeIf(s -> schedulesOverlap(day, start, end, s));
        }

        RestaurantScheduleManager.addSchedule(restaurant, day, start, end, capacity);
    }

           
    public static void updateCapacity(Restaurant restaurant, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) throws ScheduleNoExist {
        Objects.requireNonNull(day);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        boolean found = false;
        for (RestaurantSchedule s : restaurant.getRestaurantSchedules()) {
            if (scheduleInRange(day, start, end, s)) {
                s.setCapacity(capacity);
                found = true;
            }
        }
        if (!found) {
            throw new ScheduleNoExist("Le créneau à modifier n'existe pas : " + day + " " + start + " → " + end);
        }
    }

    private static boolean schedulesOverlap(DayOfTheWeek targetDay, LocalTime start, LocalTime end, RestaurantSchedule s) {
        DayOfTheWeek sDay = s.getDay();
        LocalTime sStart = s.getStart();
        LocalTime sEnd = sStart.plusMinutes(30);

        if (sDay == targetDay) {
            return intervalOverlap(start, end, sStart, sEnd);
        }
        return false;
    }

    private static boolean scheduleInRange(DayOfTheWeek targetDay, LocalTime start, LocalTime end, RestaurantSchedule s) {
        DayOfTheWeek sDay = s.getDay();
        LocalTime sStart = s.getStart();
        LocalTime sEnd = sStart.plusMinutes(30);

        if (start.equals(end)) return false;
        if (!end.isBefore(start)) {
            if (sDay != targetDay) return false;
            return intervalOverlap(start, end, sStart, sEnd);
        } else {
            if (sDay == targetDay && !sStart.isBefore(start)) return true;
            return sDay == RestaurantScheduleManager.nextDay(targetDay) && sStart.isBefore(end);
        }
    }

    private static boolean intervalOverlap(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
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

    public static DayOfTheWeek nextDay(DayOfTheWeek day){
        return switch (day) {
            case MONDAY -> DayOfTheWeek.TUESDAY;
            case TUESDAY -> DayOfTheWeek.WEDNESDAY;
            case WEDNESDAY -> DayOfTheWeek.THURSDAY;
            case THURSDAY -> DayOfTheWeek.FRIDAY;
            case FRIDAY -> DayOfTheWeek.SATURDAY;
            case SATURDAY -> DayOfTheWeek.SUNDAY;
            case SUNDAY -> DayOfTheWeek.MONDAY;
            default -> throw new IllegalArgumentException("Jour de la semaine invalide");
        };
    }
}
