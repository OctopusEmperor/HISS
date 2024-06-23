package com.example.hiss;

import java.util.ArrayList;

public class DayStatus {

    // Day of the month
    private int day;
    // Month of the year
    private int month;
    // Year
    private int year;
    // List of events for the day
    private ArrayList<Event> events;

    // Constructor to initialize day status with specified values
    public DayStatus(int day, int month, int year, ArrayList<Event> events) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.events = events;
    }

    // Default constructor to initialize day status with default values
    public DayStatus() {
        this.day = 0;
        this.month = 0;
        this.year = 0;
        this.events = new ArrayList<>();
    }

    // Getter for the day
    public int getDay() {
        return day;
    }

    // Setter for the day
    public void setDay(int day) {
        this.day = day;
    }

    // Getter for the month
    public int getMonth() {
        return month;
    }

    // Setter for the month
    public void setMonth(int month) {
        this.month = month;
    }

    // Getter for the year
    public int getYear() {
        return year;
    }

    // Setter for the year
    public void setYear(int year) {
        this.year = year;
    }

    // Getter for the list of events
    public ArrayList<Event> getEvents() {
        return events;
    }

    // Setter for the list of events
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
