package com.navroopsingh;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneRules;
import java.util.InputMismatchException;

import static org.junit.Assert.*;

/**
 * Created by navroopsingh on 11/29/15.
 */
public class CalendarTest extends Calendar {
    Calendar calendar;

    @Before
    public void setUp() throws Exception {
        calendar = new Calendar();
    }

    @Test
    public void testAddEvent() throws Exception {
        LocalDateTime eventDateTime = LocalDateTime.of(2016, 11, 26, 18, 0);
        calendar.addEvent("Thanksgiving 2016", eventDateTime, "Gather round and share the joy.");
        assertEquals(1, calendar.eventsHashMap.size());
        assertEquals(1, calendar.eventsTreeMap.size());
    }

    @Test(expected=InputMismatchException.class)
    public void testAddInvalidEvent() throws Exception {
        LocalDateTime eventDateTime = LocalDateTime.of(2017, 11, 26, 18, 0);
        calendar.addEvent("Thanksgiving 2017", eventDateTime, "Gather round again and share the joy.");
    }
    @Test
    public void testAddRecurringEvent() throws Exception {
        LocalDateTime eventDateTime = LocalDateTime.now(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-7)));
        calendar.addEvent("Daily workout", eventDateTime, "Getting in shape, one day at a time", "daily");
        assertEquals(366, calendar.eventsHashMap.size());
        assertEquals(366, calendar.eventsTreeMap.size());
    }

    @Test
    public void testUpdateEventTitle() throws Exception {
        LocalDateTime eventDateTime = LocalDateTime.of(2016, 11, 26, 18, 0);
        calendar.addEvent("Thanksgiving 2016", eventDateTime, "Gather round again and share the joy.");
        calendar.updateEventTitle("Thanksgiving 2016", eventDateTime, "Turkey Day 2016");
        assertTrue(calendar.toString().contains("Turkey Day 2016"));
        assertTrue(calendar.eventsHashMap.keySet().contains(Calendar.createEventKey("Turkey Day 2016", eventDateTime)));
        assertFalse(calendar.eventsHashMap.keySet().contains(Calendar.createEventKey("Thanksgiving 2016", eventDateTime)));
    }

    @Test
    public void testUpdateEventDateTime() throws Exception {
        LocalDateTime eventDateTime = LocalDateTime.of(2016, 11, 26, 18, 0);
        // Update the time of Thanksgiving dinner from 6:00pm to 4:00pm
        LocalDateTime updatedEventDateTime = LocalDateTime.of(2016, 11, 26, 16, 0);
        calendar.addEvent("Thanksgiving 2016", eventDateTime, "Gather round again and share the joy.");
        calendar.updateEventDateTime("Thanksgiving 2016", eventDateTime, updatedEventDateTime);
        assertTrue(calendar.toString().contains("11/26/2016 @ 4:00PM"));
        assertTrue(calendar.eventsTreeMap.containsKey(updatedEventDateTime));
        assertFalse(calendar.eventsTreeMap.containsKey(eventDateTime));
        assertTrue(calendar.eventsHashMap.containsKey(Calendar.createEventKey("Thanksgiving 2016", updatedEventDateTime)));
        assertFalse(calendar.eventsHashMap.containsKey(Calendar.createEventKey("Thanksgiving 2016", eventDateTime)));
    }

    @Test
    public void testUpdateEventNotes() throws Exception {
        LocalDateTime eventDateTime = LocalDateTime.of(2016, 11, 26, 18, 0);
        calendar.addEvent("Thanksgiving 2016", eventDateTime, "Gather round again and share the joy.");
        calendar.updateEventNotes("Thanksgiving 2016", eventDateTime, "Black Friday has turned this into a commercialized holiday.");
        assertTrue(calendar.toString().contains("Black Friday has turned this into a commercialized holiday."));
        assertFalse(calendar.toString().contains("Gather round again and share the joy."));
    }
}