package com.holdyourspot;

public record Party(String name, int size) {

    @Override
    public String toString() {
        return String.format("%s's party of %d", name, size);
    }

}
