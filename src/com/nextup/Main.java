package com.nextup;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final PartyManager partyManager = new PartyManager();

        System.out.println("\33[1;33m--- Next Up ---\33[0m");
        partyManager.print_party_size();
        partyManager.display.print_options();
    }

}
