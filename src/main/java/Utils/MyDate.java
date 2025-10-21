package Utils;

import java.time.LocalDateTime;


public class MyDate {
    //Instances
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    //Constructeur

    public MyDate(Builder builder) throws InvalidDate{
            this.day = builder.day;
            this.month = builder.month;
            this.year = builder.year;
            this.hour = builder.hour;
            this.minute = builder.minute;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MyDate myDate = (MyDate) obj;
        return day == myDate.day &&
                month == myDate.month &&
                year == myDate.year &&
                hour == myDate.hour &&
                minute == myDate.minute;
    }

    public int getDay(){
        return day;
    }

    public static MyDate now() {
        LocalDateTime now = LocalDateTime.now();
        try {
            return new MyDate.Builder()
                    .day(now.getDayOfMonth())
                    .month(now.getMonthValue())
                    .year(now.getYear())
                    .hour(now.getHour())
                    .minute(now.getMinute())
                    .build();
        } catch (InvalidDate e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isBefore(MyDate other) {
        return compareTo(other) < 0;
    }

    public boolean isAfter(MyDate other) {
        return compareTo(other) > 0;
    }

    public int compareTo(MyDate other) {
        if (year != other.year) return Integer.compare(year, other.year);
        if (month != other.month) return Integer.compare(month, other.month);
        if (day != other.day) return Integer.compare(day, other.day);
        if (hour != other.hour) return Integer.compare(hour, other.hour);
        return Integer.compare(minute, other.minute);
    }


    //Innerclass tout pareil que restaurant
    public static class Builder {
        private int day;
        private int month;
        private int year;
        private int hour;
        private int minute;

        public Builder day(int day) throws InvalidDate {
            if (day < 1 || day > 31) {
                throw new InvalidDate("Day does not exist");
            }
            this.day = day;
            return this;
        }

        public Builder month(int month) throws InvalidDate {
            if (month < 1 || month > 12) {
                throw new InvalidDate("Month does not exist");
            }
            this.month = month;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder hour(int hour) throws InvalidDate {
            if (hour < 0 || hour > 23) {
                throw new InvalidDate("Hour is wrong");
            }
            this.hour = hour;
            return this;
        }

        public Builder minute(int minute) throws InvalidDate {
            if (minute < 0 || minute > 59) {
                throw new InvalidDate("Minute is wrong");
            }
            this.minute = minute;
            return this;
        }

        public MyDate build() {
            return new MyDate(this);
        }
    }

}
