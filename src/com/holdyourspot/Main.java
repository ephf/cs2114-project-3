package com.holdyourspot;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final PartyManager partyManager = new PartyManager(scanner);

        System.out.println("\33[1;33m--- Hold Your Spot ---\33[0m");
        do partyManager.printPartySize();
        while(partyManager.performNextAction());
    }

}
