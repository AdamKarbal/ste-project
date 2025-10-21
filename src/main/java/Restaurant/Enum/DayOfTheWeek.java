package Restaurant.Enum;

import java.util.List;

public enum DayOfTheWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, SPECIAL_DAY, DAY_OFF;

    public static List<DayOfTheWeek> getRealDays() {
        return List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
    }
}
