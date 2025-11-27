package Restaurant;

import Restaurant.Enum.DayOfTheWeek;
import java.time.LocalTime;
import java.util.UUID;

public class RestaurantSchedule {
    private  String id;
    private  DayOfTheWeek day;
    private LocalTime start;
    private LocalTime end;
    private int capacity;

    public RestaurantSchedule(String id, DayOfTheWeek day) {
        this.id = id;
        this.day = day;
    }
    public RestaurantSchedule() {
    }

    public RestaurantSchedule(DayOfTheWeek day, LocalTime start, int capacity) {
        this.id = UUID.randomUUID().toString();
        this.day = day;
        this.start = normalizeStartTime(start);
        this.end = this.start.plusMinutes(30);
        this.capacity = capacity;
    }

    public RestaurantSchedule(String id, DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) {
        this.id = id;
        this.day = day;
        this.start = start;
        this.end = end;
        this.capacity = capacity;
    }

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
    public void setId(String id) { this.id = id; }
    public void setDay(DayOfTheWeek day) { this.day = day; }
    public void setStart(LocalTime start) { this.start = start; }
    public void setEnd(LocalTime end) { this.end = end; }
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
