package com.holdyourspot;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final var waitlistController = new WaitlistController();
        waitlistController.startCommandLoop();
    }

}
