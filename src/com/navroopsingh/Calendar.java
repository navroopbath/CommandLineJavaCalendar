package com.navroopsingh;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.TreeMap;


/*
 * Implementation notes:
 *   - The events in this calendar can be scheduled for up to 1 year from the
 *     current date. This mimics the behavior of an actual wall calendar.
 *
 *   - Recurring events are placed on the calendar for up to a year from now.
 *
 *   - Only one event can be scheduled for a specific time. Time ranges (e.g.
 *     6:00pm - 8:00pm) are not supported. This is to avoid collisions when
 *     using the HashMap and TreeMap.
 *
 *   - An event is uniquely identified by the string event title+event datetime
 *     (e.g. "Thanksgiving;2015-11-26T18:00:00" )
 *
 *   - This implementation is NOT threadsafe because it uses a TreeMap and HashMap.
 *     The design decision was made to implement quicker range queries since
 *     TreeMaps have good performance for such queries but are not threadsafe.
 */
public class Calendar {
    // Set the length of the calendar to 1 year
    private static final int CALENDAR_LENGTH = 1;
    // Stores mapping from the event name to event object for single event lookup
    protected HashMap<String, Event> eventsHashMap;
    // Stores mapping from event datetime to event object for range queries
    protected TreeMap<LocalDateTime, Event> eventsTreeMap;

    Calendar() {
        this.eventsHashMap = new HashMap<String, Event>();
        this.eventsTreeMap = new TreeMap<LocalDateTime, Event>();
    }

    /*
    Add a one-time scheduled event to the calendar.
     */
    public void addEvent(String eventTitle, LocalDateTime eventDateTime, String eventNotes)
                         throws InputMismatchException {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("America/Los_Angeles"));
        System.out.println(currentDateTime);
        System.out.println(eventDateTime);

        // Ensure that the event date is not further than 1 year from now
        if (currentDateTime.plusYears(CALENDAR_LENGTH).isBefore(eventDateTime) ||
                currentDateTime.isAfter(eventDateTime)) {
            throw new InputMismatchException("Event must be scheduled within one year from now.");
        }

        Event event = new Event(eventTitle, eventDateTime, eventNotes);
        // Event Title and DateTime are used to uniquely identify an event
        this.eventsHashMap.put(Calendar.createEventKey(eventTitle, eventDateTime), event);
        // Store the Event DateTime for efficient range queries
        this.eventsTreeMap.put(eventDateTime, event);
    }

    /*
     Used to add recurring events that happen daily, weekly, monthly, and yearly

     This method ensures the following:
        1. The event date is less than 1 year from today's date
        2. The event date is not before today's date.
        3. The recurringEvent is one of ["daily", "weekly", "monthly", "yearly"]
     */
    public void addEvent(String eventTitle, LocalDateTime eventDateTime, String eventNotes,
                         String recurringEvent) throws InputMismatchException {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("America/Los_Angeles"));

        // Ensure that the event date is not further than 1 year from now and that
        // the event date is not before the current date
        if (currentDateTime.plusYears(CALENDAR_LENGTH).isBefore(eventDateTime) ||
                currentDateTime.isAfter(eventDateTime)) {
            throw new InputMismatchException("Event must be scheduled within one year from now.");
        }

        do {
            Event event = new Event(eventTitle, eventDateTime, eventNotes);
            // Event Title and DateTime are used to uniquely identify an event
            this.eventsHashMap.put(Calendar.createEventKey(eventTitle, eventDateTime), event);
            // Use the Event DateTime as the key for efficient range queries
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

    /*
     * Removes the event from the array.
     */
    public Event removeEvent(String eventTitle, LocalDateTime eventDateTime) {
        String eventKey = Calendar.createEventKey(eventTitle, eventDateTime);
        if (this.eventsHashMap.containsKey(eventKey)) {
            Event event = this.eventsHashMap.get(eventKey);
            this.eventsHashMap.remove(eventKey);
            this.eventsTreeMap.remove(eventDateTime);
            return event;
        } else {
            return null;
        }
    }

    /*
     Returns the event matching eventTitle and eventDateTime and null if no
     match is found.
     */
    public Event findEvent(String eventTitle, LocalDateTime eventDateTime) {
        String eventKey = Calendar.createEventKey(eventTitle, eventDateTime);
        if (this.eventsHashMap.containsKey(eventKey)) {
            return this.eventsHashMap.get(eventKey);
        } else {
            return null;
        }
    }

    public void updateEventTitle(String eventTitle, LocalDateTime eventDateTime,
                                 String updatedTitle) {
        String eventKey = Calendar.createEventKey(eventTitle, eventDateTime);
        if (!this.eventsHashMap.containsKey(eventKey)) {
            throw new InputMismatchException("This event does not exist and cannot be updated.");
        }

        // Only updating the eventTitle
        Event event = this.eventsHashMap.get(createEventKey(eventTitle, eventDateTime));
        event.updateEventTitle(updatedTitle);
        // Replace current HashMap entry with updated title
        this.eventsHashMap.remove(Calendar.createEventKey(eventTitle, eventDateTime));
        this.eventsHashMap.put(Calendar.createEventKey(updatedTitle, eventDateTime), event);
    }

    public void updateEventDateTime(String eventTitle, LocalDateTime currentDateTime,
                                    LocalDateTime updatedDateTime) {
        String eventKey = Calendar.createEventKey(eventTitle, currentDateTime);
        if (!this.eventsHashMap.containsKey(eventKey)) {
            throw new InputMismatchException("This event does not exist and cannot be updated.");
        }

        if (this.isValidCalendarDate(updatedDateTime)) {
            Event event = this.eventsHashMap.get(eventKey);
            event.updateEventDateTime(updatedDateTime);
            this.eventsHashMap.remove(eventKey);
            this.eventsHashMap.put(Calendar.createEventKey(eventTitle, updatedDateTime), event);
            this.eventsTreeMap.remove(currentDateTime);
            this.eventsTreeMap.put(updatedDateTime, event);
        }
    }
    
    public void updateEventNotes(String eventTitle, LocalDateTime eventDateTime, String eventNotes) {
        String eventKey = Calendar.createEventKey(eventTitle, eventDateTime);
        if (!this.eventsHashMap.containsKey(eventKey)) {
            throw new InputMismatchException("This event does not exist and cannot be updated.");
        }

        Event event = this.eventsHashMap.get(eventKey);
        event.updateEventNotes(eventNotes);
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

    protected static String createEventKey(String eventTitle, LocalDateTime eventDateTime) {
        return eventTitle + ";" + eventDateTime.toString();
    }
    private static String extractEventTitleFromKey(String eventKey) {
        String[] eventKeyParts = eventKey.split(";");
        return eventKeyParts[0];
    }

    private boolean isValidCalendarDate(LocalDateTime dateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Ensure that the event date is not further than 1 year from now
        if (currentDateTime.plusYears(CALENDAR_LENGTH).isBefore(dateTime) ||
                currentDateTime.isAfter(dateTime)) {
            throw new InputMismatchException("Event must be scheduled within one year from now.");
        }

        return true;
    }
}
