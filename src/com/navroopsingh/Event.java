package com.navroopsingh;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event {
    protected String eventTitle;
    protected String eventNotes;
    protected LocalDateTime eventDateTime;


    /*
     * Constructor for Event expects eventDateAndTime to be of format
     * "MM/dd/yyyy HH:mm". This is strictly enforced for consistency.
     */
    Event(String eventTitle, LocalDateTime eventDateTime, String eventNotes) {
        this.eventTitle = eventTitle;
        this.eventNotes = eventNotes;
        this.eventDateTime = eventDateTime;
    }

    public String getEventTitle() {
        return this.eventTitle;
    }

    public String getEventNotes() {
        return this.eventNotes;
    }

    public LocalDateTime getEventDateTime() {
        return this.eventDateTime;
    }

    void updateEventTitle(String newEventTitle) {
        this.eventTitle = newEventTitle;
    }

    void updateEventDateTime(LocalDateTime updatedEventDateTime) {
        this.eventDateTime = updatedEventDateTime;
    }

    void updateEventNotes(String updatedEventNotes) {
        this.eventNotes = updatedEventNotes;
    }


    @Override
    public String toString() {
        int numCharactersBetweenPipes = 20;
        int lengthOfEventTitle = this.getEventTitle().length();
        DateTimeFormatter eventDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ h:mma");
        return this.eventDateTime.format(eventDateFormatter) + " | " +
                this.getEventTitle() +
                this.createEmptyString(numCharactersBetweenPipes - lengthOfEventTitle) +
                " | Notes: " + this.getEventNotes();
    }

    /*
     * Method that creates an empty string count characters long.
     * Used for padding output.
     */
    private static String createEmptyString(int count) {
        return new String(new char[count]).replace("\0", " ");
    }
}
