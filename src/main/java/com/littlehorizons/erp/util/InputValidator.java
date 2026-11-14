package com.littlehorizons.erp.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utility for robust console input validation.
 */
public class InputValidator {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String readString(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (required && input.isEmpty()) {
                System.out.println("  [Error] This field cannot be empty. Please try again.");
                continue;
            }
            return input;
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.printf("  [Error] Please enter a number between %d and %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  [Error] Invalid number format. Please enter an integer.");
            }
        }
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim().replace(',', '.');
            try {
                double value = Double.parseDouble(input);
                if (value < min || value > max) {
                    System.out.printf("  [Error] Value must be between %.1f and %.1f.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  [Error] Invalid number format. Please enter a valid decimal number.");
            }
        }
    }

    public static LocalDate readDate(String prompt, boolean allowDefaultToday) {
        while (true) {
            System.out.print(prompt + (allowDefaultToday ? " [Press Enter for today]: " : ": "));
            String input = SCANNER.nextLine().trim();
            if (allowDefaultToday && input.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("  [Error] Invalid date format. Please use YYYY-MM-DD (e.g., 2024-05-15).");
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = SCANNER.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes") || input.equals("s") || input.equals("si")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("  [Error] Please enter 'y' for yes or 'n' for no.");
            }
        }
    }

    public static String readOption(String prompt, String[] validOptions) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            for (String option : validOptions) {
                if (option.equalsIgnoreCase(input)) {
                    return option;
                }
            }
            System.out.println("  [Error] Invalid selection. Valid options are: " + String.join(", ", validOptions));
        }
    }

    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to return to menu...");
        SCANNER.nextLine();
    }
}
