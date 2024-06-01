package com.example.hiss;

import java.util.ArrayList;

public class DayStatus {

    int day, month, year;
    boolean status;
    ArrayList<ArrayList<Event>> events;

    public DayStatus(int day, int month, int year, boolean status, ArrayList<ArrayList<Event>> events) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.status = status;
        this.events = events;
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ArrayList<ArrayList<Event>> getEvents() {
        return events;
    }
    public void setEvents(ArrayList<ArrayList<Event>> events) {
        this.events = events;
    }
}
