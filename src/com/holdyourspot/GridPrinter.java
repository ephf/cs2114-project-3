package com.holdyourspot;

public class GridPrinter {

    public record DisplayElement(String content, int width) {}
    public record Dimensions(int width, int span, int gap) {}

    final Dimensions dimensions;
    final DisplayElement[] elements;
    final String paddingString;
    final String gapString;

    public GridPrinter(Dimensions dimensions, DisplayElement... elements) {
        this.dimensions = dimensions;
        this.elements = elements;
        paddingString = " ".repeat(dimensions.width());
        gapString = " ".repeat(dimensions.gap());
    }

    public void print() {
        int span = 0;

        for(final var element : elements) {
            System.out.printf("%s%s%s",
                    span > 0 ? gapString : "", element.content(),
                    paddingString.subSequence(element.width(), dimensions.width()));

            if(++span >= dimensions.span()) {
                span = 0;
                System.out.println();
            }
        }
    }

}
