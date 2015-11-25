package com.navroopsingh;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by navroopsingh on 11/24/15.
 */
public class EventTest {
    Event event;

    @Before
    public void setUp() throws Exception {
        this.event = new Event("Thanksgiving", "11/26/2015 18:00", "Gather round and give your thanks.");
    }

    @Test
    public void testEventCreation() {
        assertEquals("Thanksgiving", this.event.getEventTitle());
        assertEquals("Gather round and give your thanks.", this.event.getEventNotes());
        assertEquals("11/26/2015 @ 6:00PM | Thanksgiving         | Notes: Gather round and give your thanks.", this.event.toString());
    }
}