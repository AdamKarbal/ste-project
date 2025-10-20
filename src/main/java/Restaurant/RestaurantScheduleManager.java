package Restaurant;

import Restaurant.Enum.DayOfTheWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestaurantScheduleManager {
    public static List<RestaurantSchedule> getTodaySchedules(Restaurant restaurant) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return List.of();

        LocalTime now = LocalTime.now();
        DayOfTheWeek today = convertDayOfWeek(LocalDate.now().getDayOfWeek());

        return restaurant.getRestaurantSchedules().stream()
                .filter(schedule -> {
                    boolean overnight = schedule.getEnd().isBefore(schedule.getStart());
                    if (schedule.getDay() == today) {
                        if (!overnight) return schedule.getEnd().isAfter(now);
                        return true;
                    }
                    if (overnight && nextDay(schedule.getDay()) == today) {
                        return now.isBefore(schedule.getEnd());
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public static Optional<RestaurantSchedule> getNextAvailableSchedule(Restaurant restaurant) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return Optional.empty();

        LocalDateTime now = LocalDateTime.now();
        DayOfTheWeek today = convertDayOfWeek(now.getDayOfWeek());
        LocalTime currentTime = now.toLocalTime();

        return restaurant.getRestaurantSchedules().stream()
                .filter(s -> {
                    DayOfTheWeek scheduleDay = s.getDay();
                    boolean isToday = scheduleDay == today;
                    return isToday ? s.getEnd().isAfter(currentTime) : isAfterToday(today, scheduleDay);
                }).min((a, b) -> {
                    int dayDiffA = dayDifference(today, a.getDay());
                    int dayDiffB = dayDifference(today, b.getDay());
                    int cmp = Integer.compare(dayDiffA, dayDiffB);
                    if (cmp == 0) return a.getStart().compareTo(b.getStart());
                    return cmp;
                });
    }

    private static boolean isAfterToday(DayOfTheWeek today, DayOfTheWeek other) {
        return dayDifference(today, other) > 0;
    }

    private static int dayDifference(DayOfTheWeek start, DayOfTheWeek end) {
        int diff = end.ordinal() - start.ordinal();
        return diff < 0 ? diff + 7 : diff;
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

            boolean overnight1 = end1.isBefore(start1);
            boolean overnight2 = end2.isBefore(start2);

            if (!overnight1 && !overnight2) {
                boolean overlap = start2.isBefore(end1) && end2.isAfter(start1);
                if (overlap) return false;
            } else {
                if (overlapsOvernight(start1, end1, start2, end2)) return false;
            }
        }

        restaurant.addSchedule(newSchedule);
        return true;
    }

    private static boolean overlapsOvernight(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        int s1 = start1.toSecondOfDay() / 60;
        int e1 = end1.isBefore(start1) ? end1.toSecondOfDay() / 60 + 1440 : end1.toSecondOfDay() / 60;
        int s2 = start2.toSecondOfDay() / 60;
        int e2 = end2.isBefore(start2) ? end2.toSecondOfDay() / 60 + 1440 : end2.toSecondOfDay() / 60;

        return s2 < e1 && e2 > s1;
    }

    public static boolean removeScheduleById(Restaurant restaurant, String id) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        return restaurant.getRestaurantSchedules().removeIf(s -> s.getId().equals(id));
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

    public static boolean isOpenNow(Restaurant restaurant) {
        return isOpenAt(restaurant, LocalDateTime.now());
    }

    public static boolean isOpenAt(Restaurant restaurant, LocalDateTime dateTime) {
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;

        List<RestaurantSchedule> restaurantSchedules = restaurant.getRestaurantSchedules();
        if (restaurantSchedules.isEmpty()) return false;

        DayOfTheWeek currentDay = convertDayOfWeek(dateTime.getDayOfWeek());
        LocalTime time = dateTime.toLocalTime();

        return restaurantSchedules.stream().anyMatch(s -> {
            DayOfTheWeek scheduleDay = s.getDay();
            boolean overnight = s.getEnd().isBefore(s.getStart());

            if (!overnight && scheduleDay == currentDay) {
                return !time.isBefore(s.getStart()) && time.isBefore(s.getEnd());
            }

            if (overnight) {
                if (scheduleDay == currentDay && !time.isBefore(s.getStart())) return true;
                if (nextDay(scheduleDay) == currentDay && time.isBefore(s.getEnd())) return true;
            }

            return false;
        });
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

    private static DayOfTheWeek nextDay(DayOfTheWeek day) {
        return switch (day) {
            case MONDAY -> DayOfTheWeek.TUESDAY;
            case TUESDAY -> DayOfTheWeek.WEDNESDAY;
            case WEDNESDAY -> DayOfTheWeek.THURSDAY;
            case THURSDAY -> DayOfTheWeek.FRIDAY;
            case FRIDAY -> DayOfTheWeek.SATURDAY;
            case SATURDAY -> DayOfTheWeek.SUNDAY;
            case SUNDAY -> DayOfTheWeek.MONDAY;
            default -> null;
        };
    }
}
