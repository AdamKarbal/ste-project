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
        while (true) {
            if (!restaurant.alreadyScheduled(current, current.plusMinutes(30), day)) {
                restaurant.addSchedule(new RestaurantSchedule(day, current, capacity));
            }

            current = current.plusMinutes(30);

            if (current.equals(LocalTime.MIDNIGHT) || !current.isBefore(end)) {
                break; // 💥 Sortir de la boucle
            }
        }
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
