package com.holdyourspot;

import java.util.Scanner;

public class PartyManager {

    private static final int PROMPT_OPTION_WIDTH = 32;
    private static final int PROMPT_OPTION_SPAN = 3;

    private final Queue<Party> parties = new Queue<>();
    private final PromptDisplay prompter;

    public PartyManager(Scanner scanner) {
        final PromptDisplay.Dimensions dimensions = new PromptDisplay.Dimensions(PROMPT_OPTION_WIDTH, PROMPT_OPTION_SPAN);
        prompter = new PromptDisplay(dimensions, scanner,
                PromptDisplay.option("a", "(A)dd a Party"),
                PromptDisplay.option("s", "(S)eat Next Party"),
                PromptDisplay.option("v", "(V)iew Full Waitlist"),
                PromptDisplay.option("n", "View (N)ext to be Seated"),
                PromptDisplay.option("r", "(R)emove a party"),
                PromptDisplay.option("q", "(Q)uit"));
    }

    void printPartySize() {
        final int size = parties.size();
        System.out.printf("\33[36mThere %s %d Part%s Queued\33[0m\n",
                size == 1 ? "is" : "are", size, size == 1 ? "y" : "ies");
    }

    void addParty(String name, int size) {
        parties.enqueue(new Party(name, size));
    }

    void seatNextParty() {
        try {
            final Party party = parties.dequeue();
            System.out.printf("\33[36mThe next party to seat is %s's party with %d %s\n\33[0m",
                    party.name(), party.size(), party.size() == 1 ? "person" : "people");
        } catch(IndexOutOfBoundsException _) {
            System.out.println("\33[31mThere are no people queued!\33[0m");
        }
    }

    private boolean successfulFindInRemove;

    void removeParty(String nameStart) {
        successfulFindInRemove = false;

        parties.removeIf(party -> {
            if(!party.name().toLowerCase().startsWith(nameStart.toLowerCase())) return false;

            String response;
            do response = prompter.input(String.format("Are you sure you want to remove %s's party of %d? [y/n] ",
                    party.name(), party.size())).toLowerCase();
            while(switch(response) { case "y", "n" -> false; default -> true; });

            successfulFindInRemove = true;
            return response.equals("y");
        });

        if(!successfulFindInRemove) {
            System.out.printf("\33[31mThere are not parties starting with '%s'!\n\33[0m", nameStart);
        }
    }

    void printPartyList() {
        parties.enumerate((party, i) ->
            System.out.printf("\33[36m%d.\33[0m  %s \33[90m(%d)\33[0m\n", i + 1, party.name(), party.size()));
    }

    public boolean performNextAction() {
        switch(prompter.getSelection()) {
            case "a":
                System.out.println();
                addParty(
                        prompter.input("What is the name for the party?"),
                        prompter.inputPositiveInteger("How many people?"));
                System.out.println();
                break;

            case "s":
                System.out.println();
                seatNextParty();
                System.out.println();
                break;

            case "v":
                System.out.println();
                printPartyList();
                System.out.println();
                break;

            case "n":
                // TODO: View Next to be Seated
                break;

            case "r":
                System.out.println();
                removeParty(
                        prompter.input("What is the name of the party you want to remove? "));
                System.out.println();
                break;

            case "q":
                return false;
        }

        return true;
    }

}
