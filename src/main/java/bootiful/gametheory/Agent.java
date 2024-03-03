package bootiful.gametheory;

/**
 * Acts as a computer/system <br>
 * Plays at most D rounds <br>
 * Makes drops while at least 1 brick is safe <br>
 * Uses a reply: "safe" or "broken" <br>
 * @see RandomAgent
 * @see WinningAgent
 * */
public interface Agent {
    /**
     * Chooses the height where to drop a brick from
     * @return height that will be tested by dropping a brick
     * */
    int drop();

    /**
     * Changes agent's state and tracks number of bricks left
     * @param height the height where the brick is safe (true) or broken (false)
     * @param isSafe the status of a brick after the drop from the height */
    void update(int height, boolean isSafe);

    /**
     * Checks all the conditions that allow to continue dropping bricks
     * @return true if the play can be continued*/
    boolean canContinue();

    /**
     * Figures out the possible strength of each brick
     * @return prompt for a user containing the number emphasizing the strength of bricks*/
    String getGuessStrengthString();
}
