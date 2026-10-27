package com.nextup;

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

    public boolean performNextAction() {
        switch(prompter.getSelection()) {
            case "a":
                addParty(
                        prompter.input("What is the name for the party?"),
                        prompter.inputPositiveInteger("How many people?"));
                break;

            case "s":
                seatNextParty();
                break;

            case "v":
                // TODO: View Full Waitlist
                break;

            case "n":
                // TODO: View Next to be Seated
                break;

            case "r":
                // TODO: Remove a party
                break;

            case "q":
                return false;
        }

        return true;
    }

}
