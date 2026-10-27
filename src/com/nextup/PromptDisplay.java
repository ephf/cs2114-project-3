package com.nextup;

import java.util.Arrays;

public class PromptDisplay {

    public record Option(String selector, String value, int canonical_width) {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Option opt && selector.equals(opt.selector);
        }

    }

    public static Option option(String selection, String value) {
        return new Option(
                selection,
                value.replaceAll("([()])", "\33[1;90m$1\33[0m"),
                value.length());
    }

    public record Dimensions(int width, int span) {}

    final Dimensions dimensions;
    final Option[] options;
    final String spacingString;

    public PromptDisplay(Dimensions dimensions, Option... options) {
        this.dimensions = dimensions;
        this.options = options;
        spacingString = " ".repeat(dimensions.width());
    }

    public void print_options() {
        int span = 0;

        for(final Option option : options) {
            System.out.printf("   %s%s", option.value(), spacingString.substring(option.canonical_width()));

            if(++span >= dimensions.span()) {
                System.out.println();
                span = 0;
            }
        }
    }

}
