package ro.festival;

import ro.festival.database.DBInitializer;
import ro.festival.service.AuditService;
import ro.festival.service.FestivalService;
import java.time.LocalTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FestivalService festivalService = new FestivalService();

        System.out.println("Welcome to the Festival App!");
        System.out.println("We’re happy to see you!\n");

        boolean running = true;

        while (running) {
            System.out.println("Please select your role:");
            System.out.println("1 - Participant");
            System.out.println("2 - Organizer");
            System.out.println("3 - Database Tools");
            System.out.println("0 - Exit the application");
            System.out.print("Enter your choice: ");
            String roleChoice = scanner.nextLine();
            System.out.println();

            switch (roleChoice) {
                case "1" -> handleParticipantMenu(scanner, festivalService);
                case "2" -> handleOrganizerMenu(scanner, festivalService);
                case "3" -> handleDatabaseToolsMenu(scanner);
                case "0" -> {
                    System.out.println("Thank you for visiting the Festival App. See you soon!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.\n");
            }
        }

        scanner.close();
    }

    private static void handleParticipantMenu(Scanner scanner, FestivalService festivalService) {
        boolean inParticipantMenu = true;

        while (inParticipantMenu) {
            System.out.println("============= Participant Menu =============");
            // Interrogations
            System.out.println("1.  View all tickets (including discounts)");
            System.out.println("2.  View participants under 25");
            System.out.println("3.  View participation statistics (top participants & event types)");
            System.out.println("4.  Show all DJ sets on the main stage");
            System.out.println("5.  Show all-night games in FunZone");
            // Actions
            System.out.println("6.  Buy a ticket");
            System.out.println("7.  Reserve a seat for a limited-capacity event");
            System.out.println("8.  Find events that start after a specific time");
            System.out.println("9.  Festival Points: Earn & Spend");
            System.out.println("10. Join the FunZone Mini-Tournament");
            System.out.println("0.  Back to role selection");
            System.out.println("============================================");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
            System.out.println();

            switch (choice) {
                case "1" -> festivalService.printAllTickets();
                case "2" -> festivalService.printParticipantsUnder25();
                case "3" -> festivalService.viewParticipationInsights();
                case "4" -> {
                    System.out.println("DJ sets on the main stage:");
                    festivalService.printDJOnMainStage();
                }
                case "5" -> {
                    System.out.println("Games in FunZone open all night:");
                    festivalService.printAllNightGames();
                }
                case "6" -> festivalService.buyTicketInteractively(scanner);
                case "7" -> {
                    System.out.println("Reserve a seat for a limited-capacity event:");
                    festivalService.reserveSeat_GlobalTalk(scanner);
                }
                case "8" -> {
                    System.out.print("Enter time (HH:mm): ");
                    LocalTime time = LocalTime.parse(scanner.nextLine());
                    System.out.println("Events starting after " + time + ":");
                    festivalService.printEventsByStartTime(time);
                }
                case "9" -> {
                    System.out.println("Festival Points System:");
                    System.out.println("- Earn points by purchasing tickets (10% of ticket value)");
                    System.out.println("- Gain extra points by attending CampEats, GlobalTalks, or FunZone events");
                    System.out.println("- Spend points on exclusive prizes!");
                    festivalService.calculatePoints(scanner);
                }
                case "10" -> festivalService.registerAndStartMiniTournament(scanner);
                case "0" -> inParticipantMenu = false;
                default -> System.out.println("Invalid option. Please try again.\n");
            }
            System.out.println();
        }
    }

    private static void handleOrganizerMenu(Scanner scanner, FestivalService festivalService) {
        boolean inOrganizerMenu = true;

        while (inOrganizerMenu) {
            System.out.println("============== Organizer Menu ==============");
            // Interrogations
            System.out.println("1.  View full schedule for a specific day ordered by start time");
            System.out.println("2.  View all organizers and their events");
            // Actions
            System.out.println("3.  Group events by type");
            System.out.println("4.  Order all events by start time");
            System.out.println("5.  Move an event to another day");
            System.out.println("6.  Delete a participant and their ticket");
            System.out.println("7.  Sanction fake under-25 participant");
            System.out.println("8.  Swap events between two organizers");
            System.out.println("9.  Remove event with the lowest participation");
            System.out.println("10. Reassign events and remove an organizer from the system");
            System.out.println("0.  Back to role selection");
            System.out.println("============================================");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
            System.out.println();

            switch (choice) {
                case "1" -> {
                    System.out.println("What day would you like to view?");
                    System.out.println("1 - Day 1");
                    System.out.println("2 - Day 2");
                    System.out.println("3 - Day 3");
                    System.out.print("Your choice: ");
                    try {
                        int day = Integer.parseInt(scanner.nextLine());
                        if (day >= 1 && day <= 3) {
                            festivalService.printScheduleForDay(day);
                        } else {
                            System.out.println("Invalid day selected.\n");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number (1, 2, or 3).");
                    }
                }
                case "2" -> {
                    System.out.println("Organizers and their events:");
                    festivalService.printOrganizersAndEvents();
                }
                case "3" -> festivalService.groupEventsByType();
                case "4" -> {
                    System.out.println("Events ordered by start time:");
                    festivalService.printEventsOrderedByStartTime();
                }

                case "5" -> {
                    System.out.println("Move an event to another day:");
                    festivalService.moveEvent(scanner);
                }
                case "6" -> {
                    System.out.println("Delete a participant and their ticket:");
                    festivalService.deleteParticipantAndTicket(scanner);
                }
                case "7" -> {
                    System.out.println("Sanction fake under-25 participant:");
                    festivalService.sanctionFakeUnder25Claim(scanner);
                }
                case "8" -> {
                    System.out.println("Swap events between two organizers:");
                    festivalService.swapEventsBetweenOrganizers(scanner);
                }
                case "9" -> {
                    System.out.println("Remove event with the lowest participation:");
                    festivalService.deleteLeastPopularEvent();
                }
                case "10" -> {
                    System.out.println("Reassign events and remove an organizer from the system:");
                    festivalService.deleteOrganizer(scanner);
                }
                case "0" -> inOrganizerMenu = false;
                default -> System.out.println("Invalid option. Please try again.\n");
            }
            System.out.println();
        }
    }

    private static void handleDatabaseToolsMenu(Scanner scanner) {
        boolean inDBMenu = true;

        while (inDBMenu) {
            System.out.println("=========== Database Tools ===========");
            System.out.println("1. Initialize database (if not already initialized)");
            System.out.println("2. Reset database (drop + setup)");
            System.out.println("0. Back to main menu");
            System.out.println("=======================================");
            System.out.print("Enter your choice: ");
            String dbChoice = scanner.nextLine();
            System.out.println();

            switch (dbChoice) {
                case "1" -> {
                    if (!InitHelper.isInitialized()) {
                        System.out.println("The database is not initialized. Running setup...");
                        DBInitializer.setupDatabase();
                        InitHelper.setInitialized(true);
                    } else {
                        System.out.println("The database is already initialized.");
                    }
                }
                case "2" -> {
                    System.out.println("Resetting the database...");
                    DBInitializer.resetDatabase();
                    InitHelper.setInitialized(true);
                    FestivalService festivalService = new FestivalService();
                    festivalService.initDemoData();
                    AuditService.getInstance().logAction("Database has been reset and populated with demo data");
                }
                case "0" -> inDBMenu = false;
                default -> System.out.println("Invalid option. Please try again.\n");
            }
            System.out.println();
        }
    }

}
