package gh.dso;

import java.util.Scanner;

/** Small, presentation-focused helper for the examiner-friendly console UI. */
public final class ConsoleUI {
    private ConsoleUI() { }

    public static void clearHeader() {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("              GHANA COURIER SERVICE OPTIMIZER");
        System.out.println("                   DSA II SEMESTER PROJECT");
        System.out.println("================================================================");
    }

    public static void startup() {
        clearHeader();
        System.out.println("Initializing system...");
        System.out.println("  [OK] Application loaded");
        System.out.println("  [OK] Custom data structures loaded");
        System.out.println("  [OK] Search, sort and graph engines loaded");
        System.out.println("  [OK] Dispatch and optimization engines loaded");
        System.out.println("----------------------------------------------------------------");
    }

    public static void section(String title) {
        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.println("  " + title);
        System.out.println("----------------------------------------------------------------");
    }

    public static void menuItem(String number, String label) {
        System.out.printf("  [%2s] %s%n", number, label);
    }

    public static void status(String databaseStatus, String counts) {
        System.out.println("SYSTEM STATUS");
        System.out.println("  Database : " + databaseStatus);
        if (counts != null && !counts.isBlank()) {
            System.out.println("  Records  : " + counts);
        }
        System.out.println();
    }

    public static String readChoice(Scanner scanner, String prompt) {
        System.out.print("\n" + prompt + ": ");
        return scanner.nextLine().trim();
    }

    public static void pause(Scanner scanner) {
        System.out.print("\nPress ENTER to return to the menu...");
        scanner.nextLine();
    }

    public static void success(String message) {
        System.out.println("\n[OK] " + message);
    }

    public static void warning(String message) {
        System.out.println("\n[!] " + message);
    }

    public static void error(String message) {
        System.out.println("\n[ERROR] " + message);
    }
}
