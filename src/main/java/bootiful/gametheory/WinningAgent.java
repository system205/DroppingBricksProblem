package bootiful.gametheory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WinningAgent implements Agent {

    private final int maxHeight;
    private final Set<GameState> winningPositions = new HashSet<>();
    private int leftDrops;
    private int maxSafe = 1;
    private int leftBricks = 2;
    private int leastBroken;

    public WinningAgent(int height, int drops) {
        this.maxHeight = height;
        this.leftDrops = drops;
        this.leastBroken = this.maxHeight + 1;
        findAllWinningPositions();
        System.out.println("Number of winning positions: " + winningPositions.size());
    }

    private static void postOrderTraversal(GameState root, List<GameState> accumulator, Set<GameState> visited) {
        if (root.drops != 0) {// if there are children
            for (int h = root.lowestSafe + 1; h <= root.highestSafe; h++) {
                // Broken state
                GameState child;
                if (root.bricks > 0) {
                    child = new GameState(root.lowestSafe, h - 1, root.drops - 1, root.bricks - 1);
                    if (!visited.contains(child))
                        postOrderTraversal(child, accumulator, visited);
                }

                // Safe state
                child = new GameState(h, root.highestSafe, root.drops - 1, root.bricks);
                if (!visited.contains(child)) {
                    postOrderTraversal(child, accumulator, visited);
                }
            }
        }

        accumulator.add(root);
        visited.add(root);
    }

    /**
     * Drops bricks according to the winning strategy
     */
    @Override
    public int drop() {
        for (int h = maxSafe; h <= leastBroken - 1; h++) {
            if (winningPositions.contains(new GameState(h, leastBroken - 1, leftDrops - 1, leftBricks)) && // safe
                winningPositions.contains(new GameState(maxSafe, h - 1, leftDrops - 1, leftBricks - 1)))  // broken
                return h;
        }
        return 0;
    }

    @Override
    public void update(int height, boolean isSafe) {
        --leftDrops;
        if (!isSafe) {
            --leftBricks;
            leastBroken = height;
        } else {
            maxSafe = height;
        }
    }

    @Override
    public boolean canContinue() {
        return leftDrops > 0 && leftBricks > 0 && !canGuess();
    }

    @Override
    public String getGuessStrengthString() {
        String endingText;
        if (canGuess())
            endingText = "I'm 100%% sure the strength of each brick is %d drops".formatted(maxSafe);
        else {
            int randomGuess = (int) (Math.random() * (leastBroken - maxSafe) + maxSafe + 1);
            endingText = "Well.. I just hope the strength of bricks is " + randomGuess;
        }

        return endingText;
    }

    /**
     * Checks if it is possible to win with some strategy given initial state
     *
     * @return true - there is winning strategy <br> false - there is no guarantee to win
     */
    public boolean canWin() {
        return winningPositions.contains(new GameState(maxSafe, leastBroken - 1, leftDrops, leftBricks));
    }

    /**
     * @return true if it can definitely say what the brick's strength is
     */
    private boolean canGuess() {
        return leastBroken - maxSafe == 1;
    }

    /**
     * Main game analyzer. First, it inits the positions, then it goes through the tree and add children first.
     * After, it goes through the list where the children of every node are at the beginning and checks whether there is
     * such a move that hos both children in winning positions. This is what backward induction looks like.
     */
    private void findAllWinningPositions() {
        // Init winning positions with (h,h,d,b)
        for (int lowerSafe = 1; lowerSafe <= this.maxHeight; lowerSafe++) {
            for (int drops = 0; drops <= leftDrops; drops++) {
                for (int bricks = 0; bricks <= leftBricks; bricks++) {
                    if (bricks == leftBricks && lowerSafe != this.maxHeight) continue;
                    winningPositions.add(new GameState(lowerSafe, lowerSafe, drops, bricks));
                }
            }
        }

        // Compose a sorted list - children are placed before their parents
        final List<GameState> states = new ArrayList<>(maxHeight * maxHeight * leftDrops);
        postOrderTraversal(new GameState(1, maxHeight, leftDrops, leftBricks), states, new HashSet<>());

        // Do backward induction - expand winning positions set from leaf nodes to the root (initial)
        for (GameState state : states) {
            int low = state.lowestSafe;
            int high = state.highestSafe;
            int d = state.drops;
            int b = state.bricks;
            for (int h = low; h <= high; h++) {
                if (winningPositions.contains(new GameState(low, h - 1, d - 1, b - 1)) && // broken
                    winningPositions.contains(new GameState(h, high, d - 1, b))) { // safe
                    winningPositions.add(new GameState(low, high, d, b));
                }
            }
        }
    }

    private record GameState(int lowestSafe, int highestSafe, int drops, int bricks) {
    }
}


