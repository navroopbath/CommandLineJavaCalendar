package com.navroopsingh;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.TreeMap;


/*
 * Implementation notes:
 *   - The events in this calendar can be scheduled for up to 1 year from the
 *     current date. This mimics the behavior of an actual wall calendar.
 *   - Recurring events are placed on the calendar for up to a year from now.
 *   - This implementation is NOT threadsafe because it uses a TreeMap and HashMap.
 *     The design decision was made to implement quicker range queries since
 *     TreeMaps have good performance for such queries but are not threadsafe.
 */
public class Calendar {
    // Set the length of the calendar to 1 year
    private static final int CALENDAR_LENGTH = 1;
    // Stores mapping from the event name to event object for single event lookup
    HashMap<String, Event> eventsHashMap;
    // Stores mapping from event datetime to event object for range queries
    TreeMap<LocalDateTime, Event> eventsTreeMap;

    Calendar() {
        this.eventsHashMap = new HashMap<String, Event>();
        this.eventsTreeMap = new TreeMap<LocalDateTime, Event>();
    }

    /*
    Add a one-time scheduled event to the calendar.
     */
    public void addEvent(String eventTitle, LocalDateTime eventDateTime, String eventNotes) {
        Event event = new Event(eventTitle, eventDateTime, eventNotes);
        // Event Title and DateTime are used to uniquely identify an event
        this.eventsHashMap.put(this.createEventKey(eventTitle, eventDateTime), event);
        // Store the Event DateTime for efficient range queries
        this.eventsTreeMap.put(eventDateTime, event);
    }

    /*
     Used to add recurring events that happen daily, weekly, monthly, and yearly

     This method ensures the following:
        1. The event date is less than 1 year from today's date
        2. The recurringEvent is one of ["daily", "weekly", "monthly", "yearly"]
     */
    public void addEvent(String eventTitle, LocalDateTime eventDateTime, String eventNotes,
                         String recurringEvent) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Ensure that the event date is not further than 1 year from now
        if (currentDateTime.plusYears(CALENDAR_LENGTH).isBefore(eventDateTime)) {
            throw new InputMismatchException("Event must be scheduled within one year from now.");
        }

        do {
            Event event = new Event(eventTitle, eventDateTime, eventNotes);
            // Event Title and DateTime are used to uniquely identify an event
            this.eventsHashMap.put(this.createEventKey(eventTitle, eventDateTime), event);
            // Store the Event DateTime for efficient range queries
            this.eventsTreeMap.put(eventDateTime, event);
            // Create a new DateTime depending on the frequency of the event
            switch (recurringEvent) {
                case "daily":
                    eventDateTime = eventDateTime.plusDays(1);
                    break;
                case "weekly":
                    eventDateTime = eventDateTime.plusWeeks(1);
                    break;
                case "monthly":
                    eventDateTime = eventDateTime.plusMonths(1);
                    break;
                case "yearly":
                    eventDateTime = eventDateTime.plusYears(1);
                    break;
                default:
                    throw new InputMismatchException("Invalid recurring type.");
            }
        } while (eventDateTime.isBefore(currentDateTime.plusYears(CALENDAR_LENGTH)));

    }

    public void updateEvent(String eventTitle) {

    }

    public void updateEvent(String eventTitle, String eventDateAndTime) {

    }
    
    public void updateEvent(String eventTitle, String eventDateAndTime, String eventNotes) {

    }

    /*
    Returns a list of events scheduled in the Calendar.
     */
    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("Upcoming events: \n");
        for (Event event : this.eventsTreeMap.values()) {
            toReturn.append(event.toString() + "\n");
        }
        return toReturn.toString();
    }

    private String createEventKey(String eventName, LocalDateTime eventDateTime) {
        return eventName + ";" + eventDateTime.toString();
    }
    private String extractEventNameFromKey(String eventKey) {
        String[] eventKeyParts = eventKey.split(";");
        return eventKeyParts[0];
    }
}
