package com.navroopsingh;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;

/**
 * Created by navroopsingh on 11/24/15.
 */
public class EventTest {
    Event event;

    @Before
    public void setUp() throws Exception {
        LocalDateTime eventDateTime = LocalDateTime.of(2015, 11, 26, 18, 0);
        this.event = new Event("Thanksgiving 2016", eventDateTime, "Gather round and give your thanks.");
    }

    @Test
    public void testEventCreation() {
        assertEquals("Thanksgiving", this.event.getEventTitle());
        assertEquals("Gather round and give your thanks.", this.event.getEventNotes());
        assertEquals("11/26/2015 @ 6:00PM | Thanksgiving         | Notes: Gather round and give your thanks.", this.event.toString());
    }

}