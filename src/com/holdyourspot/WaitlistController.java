package com.holdyourspot;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class WaitlistController {

    private final Waitlist waitlist = new Waitlist();
    private final History<Party> history = new History<>(10);
    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleView consoleView = new ConsoleView(scanner);

    WaitlistController() {
        waitlist.addListener(event -> {
            if(event.type() == WaitlistEvent.Type.SEATED) {
                history.record(event.party());
            }
        });
    }

    private String getPartyName() {
        final var name = scanner.nextLine().trim();
        Waitlist.validateName(name);
        return name;
    }

    void startCommandLoop() {
        for(;;) {
            System.out.printf("(*) There %s %d part%s queued\n",
                    waitlist.size() == 1 ? "is" : "are", waitlist.size(), waitlist.size() == 1 ? "y" : "ies");
            final var selection = consoleView.getSelection();
            System.out.println();

            switch(selection) {
                case "a":
                    waitlist.addParty(
                            consoleView.promptUntilValid("What is the name of the party?",
                                    this::getPartyName, "Name is empty!"),
                            consoleView.promptUntilValid("How many people are in the party?",
                                    consoleView::getPositiveInteger, "Must be a positive number"));
                    break;

                case "s":
                    try {
                        final var toBeSeated = waitlist.seatNext();
                        System.out.printf("(!) Seat %s\n", toBeSeated);
                    } catch(IndexOutOfBoundsException _) {
                        ConsoleView.printError("There are no parties to be seated!\n");
                    }
                    break;

                case "v":
                    if(waitlist.isEmpty()) {
                        ConsoleView.printError("There are no parties queued!\n");
                        break;
                    }

                    waitlist.enumerate(((party, i) ->
                            System.out.printf("(%d) %s\n", i + 1, party)));
                    break;

                case "n":
                    try {
                        final var nextParty = waitlist.peekNext();
                        System.out.printf("(!) %s is up next\n", nextParty);
                    } catch(IndexOutOfBoundsException _) {
                        ConsoleView.printError("There are no parties queued!\n");
                    }
                    break;

                case "h":
                    if(history.isEmpty()) {
                        System.out.println("\33[90mHistory is empty\33[0m");
                        break;
                    }

                    history.enumerate((party, _) -> System.out.printf("(-) %s\n", party));
                    break;

                case "r":
                    final var nameSearch = consoleView.promptUntilValid("Search:",
                                    this::getPartyName, "search must not be empty")
                            .toLowerCase();

                    try {
                        waitlist.removePartyIf(party -> {
                            if(!party.name().toLowerCase().startsWith(nameSearch)) return false;
                            return consoleView.promptUntilValid(
                                    String.format("(!) Do you want to remove %s? [y/n]", party),
                                    consoleView::getYesNo, "selected a valid option");
                        });
                    } catch(NoSuchElementException _) {
                        ConsoleView.printError(String.format("Cannot find a party starting with '%s'\n", nameSearch));
                    }

                    break;

                case "q":
                    return;
            }

            System.out.println();
        }
    }

}
