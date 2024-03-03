package bootiful.gametheory;

import java.util.List;

/**
 * Main class to run the whole game
 */
public final class Application {
    private Application(){}
    public static final int H = 30 + 7 + 100; // The height of the tower
    private static final List<String> celebrations = List.of("Fantastic", "Hooray", "I knew it", "Yay", "Wonderful");
    private static final List<String> celebrationsOfBroken = List.of("Oh no", "Oops", "Ouch", "Okay", "That's sad");

    /**
     * Main method that runs the application from console
     */
    public static void start(BrickDropInterface display, final int D, final boolean randomMode) {
        // INIT PHASE
        display.addText("Maximum number of drops is set to %d%n".formatted(D));
        display.addText("Please, let me think to analyze the game\n");
        int numberOfDrops = 0;

        // Create agents
        RandomAgent randomAgent = new RandomAgent(H, D);
        WinningAgent winningAgent = new WinningAgent(H, D);

        // PLAY PHASE
        Agent playingAgent = randomMode ? randomAgent : winningAgent; // current agent

        // Check that winning strategy is possible. Otherwise, switch to just random
        if (!randomMode && !winningAgent.canWin()) {
            display.addText("""
                But it seems that my winning strategy is not always winning with maximum %d iterations 😔
                That's why I'll play randomly
                """.formatted(D));

            // Switch playing agent to Random
            playingAgent = randomAgent;
        }

        // Play the game - main loop
        while (playingAgent.canContinue()) {
            // Drop a brick
            int h = playingAgent.drop();
            display.addText("Drop #%d%nI dropped from the height: %d. Please, click whether it is 'safe' or 'broken'".formatted(++numberOfDrops, h));

            // Prompt the user to check whether the drop was safe or not
            boolean safeDrop = checkDropSafety(display);

            // Reply to an agent - update its state
            playingAgent.update(h, safeDrop);
        }

        // END PHASE
        // Say the last words of the agent guessing the strength
        final String text = playingAgent.getGuessStrengthString();
        display.displayGuess(text);
    }

    /**
     * Chooses randomly the celebration of a drop being safe from {@link Application#celebrations}
     */
    private static String celebrateSafeDrop() {
        int randomIndex = (int) (Math.random() * celebrations.size());
        return celebrations.get(randomIndex);
    }

    /**
     * Chooses randomly the celebration of a drop breaking a brick from {@link Application#celebrationsOfBroken}
     */
    private static String celebrateBrokenDrop() {
        int randomIndex = (int) (Math.random() * celebrationsOfBroken.size());
        return celebrationsOfBroken.get(randomIndex);
    }


    /**
     * Prompts a user to say 'safe' or 'broken' brick after a drop
     *
     * @return true if the reply was safe, false - broken
     */
    private static boolean checkDropSafety(BrickDropInterface display) {
        display.enableButtons();
        while (display.currentStatus == null){ // block for user change
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){}
        }
        String input = display.currentStatus;
        display.currentStatus = null;

        if (input.equals("safe")) {
            display.addText("%s. It's safe%n".formatted(celebrateSafeDrop()));
            return true;
        } else if (input.equals("broken")) {
            display.addText("%s. It's broken%n".formatted(celebrateBrokenDrop()));
            return false;
        }
        throw new IllegalStateException("Input must be 'broken' or 'safe'");
    }

}
