package bootiful.gametheory;

/**
 * Drops bricks non-deterministically
 */
public class RandomAgent implements Agent {
    private int maxSafe;
    private int minSafe = 1;
    private int leftDrops;
    private int leftBricks = 2;

    public RandomAgent(int height, int drops) {
        this.maxSafe = height;
        this.leftDrops = drops;
    }

    @Override
    public int drop() {
        return (int) (Math.random() * (maxSafe - minSafe + 1) + minSafe);
    }

    @Override
    public void update(int height, boolean isSafe) {
        --leftDrops;
        if (!isSafe) {
            --leftBricks;
            maxSafe = height - 1;
        } else {
            minSafe = height;
        }
    }

    @Override
    public boolean canContinue() {
        return leftBricks > 0 && leftDrops > 0 && minSafe != maxSafe;
    }

    @Override
    public String getGuessStrengthString() {
        String endingText = "Seems no more drops. Let me guess..";

        return """
            %s
            Hmm.. I can't say the strength for sure. But I think it is %d
            """.formatted(endingText, (int) (Math.random() * (maxSafe - minSafe) + minSafe));
    }
}
