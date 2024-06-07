package com.example.hiss;

import java.util.ArrayList;

public class DayStatus {

    int day, month, year;
    ArrayList<Event> events;

    public DayStatus(int day, int month, int year, ArrayList<Event> events) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.events = events;
    }

    public DayStatus() {
        this.day = 0;
        this.month = 0;
        this.year = 0;
        this.events = new ArrayList<>();
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
