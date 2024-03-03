# Intro
This is a **Java-Swing** based Dropping Bricks Problem game. 
- You can read a **manual** below to understand the playing flow
- **Description of methods** to get the understanding of the design and implementation
- **Game formal model** to grasp the idea of Finite Position Games, backward induction with Knaster-Tarski fix-point theorem.


# Manual

To start the application just compile and run Application class where the main method is located.
After it, the system will provide some introductory text with the game rules.
The buttons 'Safe' and 'Broken' will be enabled each time a computer wants to get a response.

The whole application flow is split into 3 parts:
1. **INIT PHASE:** A user will be asked to input necessary information by pressing 'Start' button and the game inits with it
2. **PLAY PHASE:** The agent (computer) drops bricks and ask a user to check its safety by clicking buttons.
3. **END PHASE:** The agent guesses the brick's strength and the user finishes the play

To reuse the code you can simply write your **own class implementing Agent interface**. You can call it WinningAgent and delete the previous WinningAgent class to keep the main application method untouched.

# Description of methods

The main interface of the application is Agent. It has the following methods and 2 implementations (Random and Winning):
1. int drop() - chooses the height that will be tested by dropping a brick
   1. Random - returns random number from 1 to maximum untested H
   2. Winning - searches for such a height where the drop will lead to both winning positions no matter what Strength is
2. void update(int height, boolean safe) - changes inner state (observations) of an agent
   1. Random - decreases number of left drops,  update maximum untested H and decrease number of bricks
   2. Winning - decreases number of left drops, tracks maximum safe height, and minimum unsafe (where brick is broken)
3. boolean canContinue() - checks all the conditions that allow an agent to continue dropping bricks
   1. Random - check that there are left bricks and drops (attempts)
   2. Winning - checks bricks and attempt + checks it can't state the strength already
4. String getGuessStrengthString() - formulate a prompt containing the number emphasizing the strength to be guessed
   1. Random - picks random number from 1 to maximum untested H and returns answer with doubts
   2. Winning - gives assertive output with the number if it can guess 100% OR output with doubts saying random number between maximum safe and minimum unsafe heights

Winning agent has 4 more implemented methods:
1. public boolean canWin() - checks whethe the current (initial) state is in the set of winning positions
2. private boolean canGuess() - returns true if it knows that the next height from the maximum safe height is unsafe
3. postOrderTraversal(GameNode root, List accumulator) - traverses the tree by adding first children and then the root node to the accumulator list
4. findAllWinningPositions() - initializes the winning positions as it is stated in the game formal model, uses traversal to order all the nodes and applies dynamic programming to find all the winning positions based on already calculated once
 


# Game formal model (FPG)

The states of the game look as following:
- For the computer they are: (minSafe, maxSafe, leftDrops, leftBricks). First two numbers define what are the minimum and maximum known (tested) heights to drop bricks from. The third and fourth numbers show how many drops and bricks left correspondingly.
- For the user the states are: (minSafe, maxSafe, leftDrops, leftBricks, h). It duplicates the state where the computer was + has information what height the computer dropped a brick from

So, the following moves are possible:
For computer they are: (minSafe, maxSafe, leftDrops, leftBricks) ==> (minSafe, maxSafe, leftDrops, leftBricks, h) where 'h' is between [minSafe+1, maxSafe]
For user they are: (minSafe, maxSafe, leftDrops, leftBricks, h) ==> 
- When brick is broken: (minSafe, h-1, leftDrops-1, leftBricks-1) == max safe is now one less then the drop height
- When brick is safe: (h, maxSafe, leftDrops-1, leftBricks) == min safe is not at least h

Finally, we can arrange the game tree nodes in a list where all the children of a node are placed before the parent. It is possible traversing in post order manner.
As a solution backward induction is applied, i.e. going from the initial states: (h, h, d, b) where 'h' is in [1..H], d in [0..D], b in [0,1,2] to their parents marking a parent which have a move by dropping from some h to both children being in winning positions.  