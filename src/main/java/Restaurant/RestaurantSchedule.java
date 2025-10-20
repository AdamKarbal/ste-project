package Restaurant;
import Restaurant.Enum.DayOfTheWeek;
import Utils.*;

import java.time.LocalTime;
import java.util.UUID;

public class RestaurantSchedule {
    //Instances
    private final String id;
    private final DayOfTheWeek day;
    private LocalTime start;
    private LocalTime end;
    private int capacity;

    //Constructeur
    public RestaurantSchedule(DayOfTheWeek day, LocalTime start, LocalTime end, int capacity){
        this.id = UUID.randomUUID().toString();
        this.day = day;
        this.start=start;
        this.end=end;
        this.capacity=capacity;
    }

    public String getId() {
        return id;
    }
    //Getter & setter
    public DayOfTheWeek getDay() {
        return day;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
