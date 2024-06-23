package com.example.hiss;

public class Event {

    // Title of the event
    private String title;
    // Description of the event
    private String description;
    // Time of the event
    private String time;

    // Getter for the title
    public String getTitle() {
        return title;
    }

    // Setter for the title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for the description
    public String getDescription() {
        return description;
    }

    // Setter for the description
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter for the time
    public String getTime() {
        return time;
    }

    // Setter for the time
    public void setTime(String time) {
        this.time = time;
    }

    // Constructor to initialize event with specified values
    public Event(String title, String description, String time) {
        this.title = title;
        this.description = description;
        this.time = time;
    }

    // Default constructor to initialize event with default values
    public Event() {
        this.title = title;
        this.description = description;
        this.time = time;
    }
}
