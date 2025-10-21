package Restaurant;

import Restaurant.Enum.DayOfTheWeek;
import java.time.LocalTime;
import java.util.UUID;

public class RestaurantSchedule {
    private final String id;
    private final DayOfTheWeek day;
    private LocalTime start;
    private LocalTime end;
    private int capacity;

    // Constructeur simplifié : on ne passe plus end
    public RestaurantSchedule(DayOfTheWeek day, LocalTime start, int capacity) {
        this.id = UUID.randomUUID().toString();
        this.day = day;
        this.start = normalizeStartTime(start);
        this.end = this.start.plusMinutes(30); // toujours 30 min
        this.capacity = capacity;
    }

    /**
     * Arrondit l'heure à l'heure pile ou à la demi-heure la plus proche
     */
    private LocalTime normalizeStartTime(LocalTime time) {
        int minute = time.getMinute();
        if (minute < 15) minute = 0;
        else if (minute < 45) minute = 30;
        else {
            time = time.plusHours(1);
            minute = 0;
        }
        return LocalTime.of(time.getHour() % 24, minute);
    }

    public String getId() { return id; }
    public DayOfTheWeek getDay() { return day; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }
    public int getCapacity() { return capacity; }

    public void setCapacity(int capacity) { this.capacity = capacity; }

    public void addCapacity(int capacity){
        this.capacity+=capacity;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + day + " | " +
                "Début : " + start + " - Fin : " + end + " | Capacité : " + capacity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RestaurantSchedule other = (RestaurantSchedule) obj;
        return start.equals(other.start) && end.equals(other.end);
    }
}
