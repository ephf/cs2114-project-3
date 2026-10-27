package com.nextup;

public class PartyManager {

    private static final int PROMPT_OPTION_WIDTH = 32;
    private static final int PROMPT_OPTION_SPAN = 3;

    private final Queue<Party> parties = new Queue<>();
    final PromptDisplay display;

    public PartyManager() {
        final PromptDisplay.Dimensions dimensions = new PromptDisplay.Dimensions(PROMPT_OPTION_WIDTH, PROMPT_OPTION_SPAN);
        display = new PromptDisplay(dimensions,
                PromptDisplay.option("a", "(A)dd a Party"),
                PromptDisplay.option("s", "(S)eat Next Party"),
                PromptDisplay.option("v", "(V)iew Full Waitlist"),
                PromptDisplay.option("n", "View (N)ext to be Seated"),
                PromptDisplay.option("r", "(R)emove a party"),
                PromptDisplay.option("q", "(Q)uit"));
    }

    void print_party_size() {
        final int size = parties.size();
        System.out.printf("\33[36mThere are %d Part%s Queued\33[0m\n", size, size == 1 ? "y" : "ies");
    }

    void print_prompts() {

    }

}
