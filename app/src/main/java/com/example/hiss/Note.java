package com.example.hiss;

// Represents a note with a title and description.
public class Note {

    private String title;
    private String description;

    /**
     * Constructs a Note object with the specified title and description.
     *
     * @param title The title of the note.
     * @param description The description of the note.
     */
    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Constructs a Note object with default values.
     */
    public Note() {
        this.title = title;
        this.description = description;
    }

    /**
     * Returns the title of the note.
     *
     * @return The title of the note.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the note.
     *
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the description of the note.
     *
     * @return The description of the note.
     */
    public String getDescription() {
        return description;
    }

/**
 * Sets the description of the note.
 ** @param description The description to set.
 * */
    public void setDescription(String description) {
        this.description = description;
    }

}
