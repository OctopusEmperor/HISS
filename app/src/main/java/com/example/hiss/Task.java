package com.example.hiss;

public class Task {

    private String title;
    private String description;
    private String urgency;
    private boolean status;

    public Task(String title, String description, String urgency, boolean status) {
        this.title = title;
        this.description = description;
        this.urgency = urgency;
        this.status = status;
    }

    public Task(){
        this.title = title;
        this.description = description;
        this.urgency = urgency;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrgency() {
        return urgency;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}