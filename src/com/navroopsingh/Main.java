package com.navroopsingh;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    private static final ArrayList<String> commands =
            new ArrayList<String>(Arrays.asList("view", "insert", "delete", "update", "exit"));
    static final HashSet commandsSet = new HashSet(commands);
    Calendar calendar;
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main.printWelcomeMessage();
        Main mainProgram = new Main();
        mainProgram.beginCalendarProgram();
    }

    private void beginCalendarProgram() {
        calendar = new Calendar();
        String application_status = null;

        while (application_status != "exit") {
            System.out.print(">>> ");
            String input = scanner.nextLine();
            input = input.trim();
            String[] input_parts = input.split(" ");

            if (input_parts.length == 1) {
                // The only valid single word command is "exit"
                if (input_parts[0].equals("exit")) {
                    application_status = "exit";
                } else {
                    System.out.println("Invalid command. Please try again.");
                }
            } else if (input_parts.length > 1) {
                // extract the operation type
                String operation_type = input_parts[0].toLowerCase();
                String event_indicator = input_parts[1].toLowerCase();

                if (!commandsSet.contains(operation_type) || !event_indicator.matches("event[s]?$")) {
                    // The user entered an invalid operation type or event indicator
                    System.out.println("Invalid command. Please try again.");
                } else {
                    // Call the appropriate handler method
                    switch (operation_type) {
                        case "view":
                            viewCalendar(event_indicator);
                            break;
                        case "insert":
                            insertIntoCalendar();
                            break;
                        case "delete":
                            deleteFromCalendar();
                            break;
                        case "update":
                            updateInCalendar();
                            break;
                        default: break;
                    }
                }
            } else { // The user did not type in at least one valid word
                System.out.println("Invalid command. Please try again.");
            }
        }

    }

    private static void printWelcomeMessage() {
        String welcome_message =
                "**********************************************\n" +
                        "  Welcome to the Command Line Calendar\n" +
                        "**********************************************\n" +
                        "  Usage is as follows:\n" +
                        "  \n" +
                        "  * View all Calendar events or events in a range of dates * \n" +
                        "   view events\n" +
                        "\n" +
                        "   * View a specific Calendar event *\n" +
                        "   view event\n" +
                        "\n" +
                        "   * Insert an event *\n" +
                        "   insert event\n" +
                        "   \n" +
                        "   * Delete event *\n" +
                        "   delete event\n" +
                        "\n" +
                        "   * Update event *\n" +
                        "   update event\n" +
                        "\n" +
                        "   * Exit Calendar application *\n" +
                        "   exit\n" +
                        "\n" +
                        "Follow command prompts for these commands to access specific commands.\n\n";
        System.out.println(welcome_message);
    }

    private void viewCalendar(String event_indicator) {
        if (event_indicator.equals( "event" )) {
            // Uniquely identify the event from user input
            ArrayList eventInfo = uniquelyIdentifyEvent();
            String eventTitle = (String) eventInfo.get(0);
            LocalDateTime eventDateTime = (LocalDateTime) eventInfo.get(1);

            // Fetch the event from the calendar
            Event event = calendar.findEvent(eventTitle, eventDateTime);
            if (event == null) {
                System.out.println("    Event not found. Try again");
            } else {
                System.out.println(event.toString());
            }
        } else if (event_indicator.equals( "events" )) {
            // Just print out the entire calendar for now. Add functionality for
            // range search later.
            System.out.println(calendar);
        }
    }

    private void insertIntoCalendar() {
        // Capture event title and event date and time from user input
        ArrayList eventInfo = uniquelyIdentifyEvent();
        String eventTitle = (String) eventInfo.get(0);
        LocalDateTime eventDateTime = (LocalDateTime) eventInfo.get(1);

        // Capture event notes from user input
        String eventNotes = "";
        while (!eventNotes.matches("[\\w\\p{Punct} ]+")) {
            System.out.print("      Enter event notes (can contain letters, digits, punctuation, and spaces): ");
            eventNotes = scanner.nextLine();
            if (!eventNotes.matches("[\\w\\p{Punct} ]+")) {
                System.out.println("        Incorrect format for event notes. Try again.");
            }
        }

        // Capture the event recurring type from user input
        String repeatType = "";
        while (!repeatType.matches("\\bnone|daily|weekly|monthly|yearly\\b")) {
            System.out.print("      Enter how often the event is recurring: (one of [none, daily, weekly, monthly, yearly] ): ");
            repeatType = scanner.nextLine();
        }

        // Schedule new event on calendar from obtained user input
        try { // Try to schedule an event with the user-set parameters
            if (repeatType.matches("none$")) {
                calendar.addEvent(eventTitle, eventDateTime, eventNotes);
            } else { // schedule a recurring event
                calendar.addEvent(eventTitle, eventDateTime, eventNotes, repeatType);
            }
        } catch (InputMismatchException e) {
            System.out.println("        Error while creating event. Event date must be within one year of today's date.");
        }
    }

    private void deleteFromCalendar() {
        System.out.println("Which event would you like to delete?\n");
        // Capture event title and event date and time from user input
        ArrayList eventInfo = uniquelyIdentifyEvent();
        String eventTitle = (String) eventInfo.get(0);
        LocalDateTime eventDateTime = (LocalDateTime) eventInfo.get(1);

        // Fetch the event from the calendar
        Event event = calendar.findEvent(eventTitle, eventDateTime);
        if (event == null) {
            System.out.println("    Event not found. Try again");
            return;
        }

        calendar.removeEvent(eventTitle, eventDateTime);
        System.out.printf("The following event has been removed: %s\n", event);
    }

    private void updateInCalendar() {
        System.out.println("Which event would you like to update?\n");
        // Capture event title and event date and time from user input
        ArrayList eventInfo = uniquelyIdentifyEvent();
        String eventTitle = (String) eventInfo.get(0);
        LocalDateTime eventDateTime = (LocalDateTime) eventInfo.get(1);

        // Fetch the event from the calendar
        Event event = calendar.findEvent(eventTitle, eventDateTime);
        if (event == null) {
            System.out.println("    Event not found. Try again");
            return;
        }

        String field_to_update = "";
        while (!field_to_update.matches("\\btitle|date|time|notes\\b")) {
            // Grab the field the user wants to update: event title, event date or time, or event notes
            System.out.print("      Enter the field you want to update: (one of [title, date, time, notes]): ");
            field_to_update = scanner.nextLine();
            if (!field_to_update.matches("\\btitle|date|time|notes\\b")) {
                System.out.println("        Invalid field type specified. Try again.");
            }
        }

        if (field_to_update.equals("title")) {
            // Get the new event title from user input
            String newEventTitle  = "";
            while (newEventTitle.equals("") || !eventTitle.matches("[\\w\\p{Punct} ]+")) {
                System.out.print("      Enter the new event title (can contain letters, digits, punctuation, and spaces): ");
                newEventTitle = scanner.nextLine();
            }
            // Update the event title
            calendar.updateEventTitle(eventTitle, eventDateTime, newEventTitle);
        } else if (field_to_update.equals("date") || field_to_update.equals("time")) {
            // Get the new event DateTime from user input
            LocalDateTime newEventDateTime = parseDateTime(eventTitle);

            // Update the event DateTime
            try {
                calendar.updateEventDateTime(eventTitle, eventDateTime, newEventDateTime);
            } catch (InputMismatchException e) {
                System.out.println("Invalid date and time entered. Try updating event again");
            }
        } else if (field_to_update.equals("notes")) {
            // Capture new user event notes from user input
            String eventNotes = "";
            while (!eventNotes.matches("[\\w\\p{Punct} ]+")) {
                System.out.print("      Enter new event notes (can contain letters, digits, punctuation, and spaces): ");
                eventNotes = scanner.nextLine();
                if (!eventNotes.matches("[\\w\\p{Punct} ]+")) {
                    System.out.println("        Incorrect format for event notes. Try again.");
                }
            }
            calendar.updateEventNotes(eventTitle, eventDateTime, eventNotes);
        } else {
            System.out.println("Error while updating event. Please try again.");
        }
    }

    /*
     * Returns a ArrayList of length 2 with [eventTitle, eventDateTime]. The
     * event title is extracted from user input and a LocalDateTime object (eventDateTime)
     * is created from user input as well.
     */
    private ArrayList uniquelyIdentifyEvent() {
        System.out.println("Enter the following fields with the exact format indicated: \n");

        // Get event title from user input, which is only allowed to contain word characters
        String eventTitle  = "";
        while (!eventTitle.matches("[\\w\\p{Punct} ]+")) {
            System.out.print("      Enter the event title (can contain letters, digits, punctuation, and spaces): ");
            eventTitle = scanner.nextLine();
        }

        // Get event DateTime for the user
        LocalDateTime eventDateTime = parseDateTime(eventTitle);

        ArrayList eventInfo = new ArrayList();
        eventInfo.add(0, eventTitle);
        eventInfo.add(1, eventDateTime);
        return eventInfo;
    }

    private LocalDateTime parseDateTime(String eventTitle) {
        // Create a LocalDateTime object from user input
        LocalDateTime eventDateTime = null;
        while (eventDateTime == null) {
            // Get the event's Date from the user
            String eventDate = "";
            while (!eventDate.matches("\\d{2}/\\d{2}/\\d{4}$")) {
                System.out.print("      Enter the event date (MM/dd/yyyy): ");
                eventDate = scanner.nextLine();
                if (!eventDate.matches("\\d{2}/\\d{2}/\\d{4}$")) {
                    System.out.println("        \nEntered invalid date format. Try again. \n");
                }
            }

            // Get the event's Time from the user
            String eventTime = "";
            while (!eventTime.matches("\\d{1,}:\\d{2} pm|am")) {
                System.out.print("      Enter the event time (hh:mm am|pm): ");
                eventTime = scanner.nextLine();
                if (!eventTime.matches("\\d{1,}:\\d{2} pm|am")) {
                    System.out.print("        \nEntered invalid time format. Try again. \n");
                }
            }

            // Parse the strings to create a LocalDateTime object
            String[] dateParts = eventDate.split("/");
            String[] timeParts = eventTime.split(" ");
            int month = Integer.parseUnsignedInt(dateParts[0]);
            int dayOfMonth = Integer.parseUnsignedInt(dateParts[1]);
            int year = Integer.parseUnsignedInt(dateParts[2]);
            int hour = Integer.parseUnsignedInt(timeParts[0].split(":")[0]);
            int minute = Integer.parseUnsignedInt(timeParts[0].split(":")[1]);
            String meridiem = timeParts[1];

            try {
                hour = this.convert_hour_to_24hr_clock(hour, meridiem);
                eventDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
            } catch (DateTimeException e) {
                System.out.printf("        \nError occurred while creating event %s on %d/%d/%d." +
                        " Try again with valid date.\n", eventTitle, month, dayOfMonth, year);
                // Run the loop again to ask the user for a valid date
            }
        }

        return eventDateTime;
    }

    private int convert_hour_to_24hr_clock(int hour, String meridiem) {
        if (meridiem.equals("pm") && hour == 12) {
            return hour;
        } else if (meridiem.equals("am") && hour == 12) {
            return 0;
        } else if (meridiem.equals("pm")) {
            return 12 + hour;
        } else {
            return hour;
        }
    }
}
