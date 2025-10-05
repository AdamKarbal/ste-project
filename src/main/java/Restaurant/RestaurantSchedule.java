package Restaurant;
import Utils.*;
public class RestaurantSchedule {
    //Instances
    private Date start;
    private Date end;
    private int capacity;

    //Constructeur
    public RestaurantSchedule(Date start, Date end, int capacity){
        this.start=start;
        this.end=end;
        this.capacity=capacity;
    }

    //Getter & setter


    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        capacity = capacity;
    }

    @Override
    public String toString() {
        return "Début : " + start + " Fin : " + end + " Capacité : " + capacity + ".\n";
    }
}
