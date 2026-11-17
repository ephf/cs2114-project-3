package com.holdyourspot;

import java.util.Locale;
import java.util.Scanner;

public class ConsoleView {

    private static final int OPTIONS_WIDTH = 32;
    private static final int OPTIONS_SPAN = 3;
    private static final int OPTIONS_GAP = 3;

    private record Option(String selector, String label) {}

    final Option[] options = new Option[]{
            new Option("a", "(A)dd a Party"),
            new Option("s", "(S)eat Next Party"),
            new Option("v", "(V)iew Full Waitlist"),
            new Option("n", "View (N)ext to be Seated"),
            new Option("h", "View Seating (H)istory"),
            new Option("r", "(R)emove a party"),
            new Option("q", "(Q)uit"),
    };
    final GridPrinter optionsPrinter;
    final Scanner scanner;

    public ConsoleView(Scanner scanner) {
        this.scanner = scanner;

        final var elements = new GridPrinter.DisplayElement[options.length];
        for(int i = 0; i < options.length; i++) {
            elements[i] = new GridPrinter.DisplayElement(
                    options[i].label().replaceAll("([()])", "\33[1;90m$1\33[0m"),
                    options[i].label().length());
        }

        optionsPrinter = new GridPrinter(
                new GridPrinter.Dimensions(OPTIONS_WIDTH, OPTIONS_SPAN, OPTIONS_GAP), elements);
    }

    public static void printError(String error) {
        System.out.printf("\33[31m%s\33[0m", error);
    }

    public<T> T promptUntilValid(String prompt, PossiblyInvalidSupplier<T, ? extends Exception> supplier,
                                 String message) {
        for(Exception exception = null;;) {
            System.out.printf("\33[36m%s \33[0m", prompt);
            if(exception != null) printError("(" + message + ") ");

            try {
                return supplier.get();
            } catch(Exception e) {
                exception = e;
            }
        }
    }

    public String validateSelection(String selector) throws UnsupportedOperationException {
        for(final var option : options) {
            if(option.selector().equals(selector)) return selector;
        }
        throw new UnsupportedOperationException();
    }

    private String getValidSelection() throws UnsupportedOperationException {
        return validateSelection(scanner.nextLine().trim().toLowerCase());
    }

    public String getSelection() {
        optionsPrinter.print();
        return promptUntilValid("Select an option above", this::getValidSelection, "invalid option");
    }

    @FunctionalInterface
    public interface PossiblyInvalidSupplier<T, E extends Exception> {
        T get() throws E;
    }

    public int getPositiveInteger() throws NumberFormatException {
        final int integer = Integer.parseInt(scanner.nextLine().trim());
        if(integer > 0) return integer;
        throw new NumberFormatException();
    }

    public boolean getYesNo() throws IllegalArgumentException {
        return switch(scanner.nextLine().trim().toLowerCase()) {
            case "y" -> true;
            case "n" -> false;
            default -> throw new IllegalArgumentException();
        };
    }

}
