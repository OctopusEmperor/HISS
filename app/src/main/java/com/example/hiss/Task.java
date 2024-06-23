package com.example.hiss;

/**
 * Represents a task with a title, description, urgency, and status.
 */
public class Task {

    private String title;
    private String description;
    private String urgency;
    private boolean status;

    /**
     * Constructs a Task object with the specified title, description,
     urgency, and status.
     *
     * @param title The title of the task.
     * @param description The description of the task.
     * @param urgency The urgency of the task.
     * @param status The status of the task.
     */
    public Task(String title, String description, String urgency, boolean status) {
        this.title = title;
        this.description = description;
        this.urgency = urgency;
        this.status = status;
    }

    /**
     * Constructs a Task object with default values.
     */
    public Task(){
        this.title = title;
        this.description = description;
        this.urgency = urgency;
        this.status = status;
    }

    /**
     * Returns the title of the task.
     *
     * @return The title of the task.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the description of the task.
     *
     * @return The description of the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the urgency of the task.
     *
     * @return The urgency of the task.
     */
    public String getUrgency() {
        return urgency;
    }

    /**
     * Returns the status of the task.
     *
     * @return The status of the task.
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     *
     * @param status The status to set.
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
}