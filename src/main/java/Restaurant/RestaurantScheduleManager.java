package Restaurant;

import Api.Utils.Requester;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Exception.ScheduleNoExist;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class RestaurantScheduleManager {
    public RestaurantScheduleManager(){};

    public List<RestaurantSchedule> getSchedules(String restaurantName) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null)
            return List.of();

        return restaurant.getRestaurantSchedules();
    }

    public List<RestaurantSchedule> getTodaySchedules(String restaurantName) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
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

    public Optional<RestaurantSchedule> getAvailableScheduleFor(String restaurantName, LocalTime requestedTime) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null || requestedTime == null)
            return Optional.empty();

        DayOfTheWeek today = convertDayOfWeek(LocalDate.now().getDayOfWeek());

        return restaurant.getRestaurantSchedules().stream()
                .filter(s -> s.getDay() == today)
                .filter(s -> !requestedTime.isBefore(s.getStart()) &&
                        requestedTime.isBefore(s.getStart().plusMinutes(30))) // use start + 30min
                .filter(s -> s.getCapacity() > 0)
                .findFirst();
    }

    public boolean addSchedule(String restaurantName, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null || start == null || end == null || day == null)
            return false;

        if (!end.isBefore(start)) {
            createIntervals(restaurant, day, start, end, capacity);
        } else {
            createIntervals(restaurant, day, start, LocalTime.of(23, 30), capacity);
            createIntervals(restaurant, nextDay(day), LocalTime.MIDNIGHT, end, capacity);
        }

        return true;
    }


    private void createIntervals(Restaurant restaurant, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) {
        LocalTime current = start;
        do {
            if (!restaurant.alreadyScheduled(current, current.plusMinutes(30), day)) {
                restaurant.addSchedule(new RestaurantSchedule(day, current, capacity));
            }
            current = current.plusMinutes(30);
        } while (!current.equals(LocalTime.MIDNIGHT) && current.isBefore(end));
    }


    public void updateSchedule(String restaurantName, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        Objects.requireNonNull(day);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        List<RestaurantSchedule> schedules = restaurant.getRestaurantSchedules();
        if (schedules != null) {
            schedules.removeIf(s -> schedulesOverlap(day, start, end, s));
        }

        addSchedule(restaurantName, day, start, end, capacity);
    }

           
    public void updateCapacity(String restaurantName, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) throws ScheduleNoExist {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
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

    private boolean schedulesOverlap(DayOfTheWeek targetDay, LocalTime start, LocalTime end, RestaurantSchedule s) {
        DayOfTheWeek sDay = s.getDay();
        LocalTime sStart = s.getStart();
        LocalTime sEnd = sStart.plusMinutes(30);

        if (sDay == targetDay) {
            return intervalOverlap(start, end, sStart, sEnd);
        }
        return false;
    }

    private boolean scheduleInRange(DayOfTheWeek targetDay, LocalTime start, LocalTime end, RestaurantSchedule s) {
        DayOfTheWeek sDay = s.getDay();
        LocalTime sStart = s.getStart();
        LocalTime sEnd = sStart.plusMinutes(30);

        if (start.equals(end)) return false;
        if (!end.isBefore(start)) {
            if (sDay != targetDay) return false;
            return intervalOverlap(start, end, sStart, sEnd);
        } else {
            if (sDay == targetDay && !sStart.isBefore(start)) return true;
            return sDay == nextDay(targetDay) && sStart.isBefore(end);
        }
    }

    private boolean intervalOverlap(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }

    public boolean removeScheduleById(String restaurantName, String id) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        return restaurant.getRestaurantSchedules().removeIf(s -> s.getId().equals(id));
    }

    public int getCapacityById(String restaurantName, String id) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) throw new RuntimeException("Erreur");
        for (RestaurantSchedule s : restaurant.getRestaurantSchedules()) {
            if (s.getId().equals(id)) return s.getCapacity();
        }
        throw new ScheduleNoExist("ce schedule n'existe pas");
    }

    public boolean checkCapacity(String restaurantName,String id){
        return getCapacityById(restaurantName, id)>0;
    }

    public boolean updateScheduleById(String restaurantName, String id, DayOfTheWeek targetDay, LocalTime start, LocalTime end) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        for (RestaurantSchedule s : restaurant.getRestaurantSchedules()) {
            if (s.getId().equals(id)) {
                s.setDay(targetDay);
                s.setStart(start);
                s.setEnd(end);
                return true;
            }
        }
        return false;
    }

    public boolean updateCapacityById(String restaurantName, String id, int capacity) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        for (RestaurantSchedule s : restaurant.getRestaurantSchedules()) {
            if (s.getId().equals(id)) {
                s.setCapacity(capacity);
                return true;
            }
        }
        return false;
    }

    public boolean incrementCapacityById(String restaurantName, String id, int capacity) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;
        for (RestaurantSchedule s : restaurant.getRestaurantSchedules()) {
            if (s.getId().equals(id)) {
                s.addCapacity(capacity);
                return true;
            }
        }
        return false;
    }

    public boolean isOpenNow(String restaurantName) {
        return isOpenAt(restaurantName, LocalDateTime.now());
    }

    public boolean isOpenAt(String restaurantName, LocalDateTime dateTime) {
        Restaurant restaurant = Requester.getRestaurantByName(restaurantName);
        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;

        LocalTime time = dateTime.toLocalTime();
        DayOfTheWeek today = convertDayOfWeek(dateTime.getDayOfWeek());

        return restaurant.getRestaurantSchedules().stream().anyMatch(s ->
                s.getDay() == today &&
                        !time.isBefore(s.getStart()) &&
                        time.isBefore(s.getStart().plusMinutes(30))
        );
    }

    public DayOfTheWeek convertDayOfWeek(DayOfWeek dow) {
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

    private DayOfTheWeek nextDay(DayOfTheWeek day){
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
