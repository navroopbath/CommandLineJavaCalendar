package com.navroopsingh;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

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

            if (input.length() == 1) {
                // The only valid single word command is "exit"
                if (input == "exit") {
                    application_status = "exit";
                } else {
                    System.out.println("Invalid command. Please try again.");
                }
            } else if (input.length() > 1) {
                String[] input_parts = input.split(" ");
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
                        case "insert":
                            ;
                        case "delete":
                            ;
                        case "update":
                            ;
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

    protected void viewCalendar(String event_indicator) {
        if (event_indicator.matches("event")) {
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
        while (!eventTitle.matches("\\w+")) {
            System.out.print("      Enter the event title (word characters: [a-zA-Z_0-9]]): ");
            eventTitle = scanner.nextLine();
        }

        // Create a LocalDateTime object from user input
        LocalDateTime eventDateTime = null;
        while (eventDateTime == null) {
            // Get the event's Date from the user
            String eventDate = "";
            while (!eventDate.matches("\\d{2}/\\d{2}/\\d{4}$")) {
                System.out.print("      Enter the event date (MM/dd/yyyy): ");
                eventDate = scanner.nextLine();
            }

            // Get the event's Time from the user
            String eventTime = "";
            while (!eventTime.matches("\\d{1,}:\\d{2} pm|am")) {
                System.out.print("      Enter the event time (h:mm am|pm): ");
                eventTime = scanner.nextLine();
            }

            // Parse the strings to create a LocalDateTime object
            String[] dateParts = eventDate.split("/");
            String[] timeParts = eventTime.split(" ");
            int month = Integer.parseUnsignedInt(dateParts[0]);
            int dayOfMonth = Integer.parseUnsignedInt(dateParts[1]);
            int year = Integer.parseUnsignedInt(dateParts[2]);
            int hour = Integer.parseUnsignedInt(timeParts[0].split(":")[0]);
            int minute = Integer.parseUnsignedInt(timeParts[0].split(":")[1]);

            try {
                eventDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
            } catch (DateTimeException e) {
                continue; // Run the loop again to ask the user for a valid date
            }
        }

        ArrayList eventInfo = new ArrayList();
        eventInfo.add(0, eventTitle);
        eventInfo.add(1, eventDateTime);
        return eventInfo;
    }
}
