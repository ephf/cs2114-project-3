package com.holdyourspot;

import java.util.Scanner;

public class PromptDisplay {

    public record Option(String selector, String value, int canonical_width) {}

    public static Option option(String selection, String value) {
        return new Option(
                selection,
                value.replaceAll("([()])", "\33[1;90m$1\33[0m"),
                value.length());
    }

    public record Dimensions(int width, int span) {}

    final Dimensions dimensions;
    final Scanner scanner;
    final Option[] options;
    final String spacingString;

    public PromptDisplay(Dimensions dimensions, Scanner scanner, Option... options) {
        this.dimensions = dimensions;
        this.scanner = scanner;
        this.options = options;
        spacingString = " ".repeat(dimensions.width());
    }

    public void printOptions() {
        int span = 0;

        for(final Option option : options) {
            System.out.printf("   %s%s", option.value(), spacingString.substring(option.canonical_width()));

            if(++span >= dimensions.span()) {
                System.out.println();
                span = 0;
            }
        }
    }

    public boolean hasOption(String selection) {
        for(final Option option : options) {
            if(option.selector.equals(selection)) return true;
        }

        return false;
    }

    public String parseScannerLine() {
        String input;
        while(!hasOption(input = scanner.nextLine().trim().toLowerCase())) {
            System.out.print("\33[31mPlease Select a Valid Option!\33[0m ");
        }

        return input;
    }

    public String getSelection() {
        printOptions();
        return parseScannerLine();
    }

    public String input(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine().trim();
    }

    public int inputPositiveInteger(String prompt) {
        for(boolean once = true;; once = false) {
            System.out.printf("%s %s(int)\33[0m ", prompt, once ? "" : "\33[31m");
            String input = scanner.nextLine().trim();

            try {
                int integer = Integer.parseInt(input);
                if(integer <= 0) continue;
                return integer;
            } catch (NumberFormatException _) {}
        }
    }

}
