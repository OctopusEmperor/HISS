package com.example.hiss;

public class Date {

    // Day of the month
    private int day;
    // Year
    private int year;
    // Month as a String
    private String month;

    // Constructor to initialize the date
    public Date(int day, String month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // Default constructor
    public Date() {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // Getter for the day
    public int getDay() {
        return day;
    }

    // Getter for the year
    public int getYear() {
        return year;
    }

    // Getter for the month
    public String getMonth() {
        return month;
    }
}
