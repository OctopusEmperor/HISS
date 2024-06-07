package com.example.hiss;

public class Event {

    private String title, description, time;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Event(String title, String description, String time) {
        this.title = title;
        this.description = description;
        this.time = time;
    }
     public Event(){
         this.title = title;
         this.description = description;
         this.time = time;
     }
}
