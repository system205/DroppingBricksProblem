package bootiful.gametheory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BrickDropInterface extends JFrame {
    public volatile String currentStatus;
    private JTextField numberOfDropsField;
    private JComboBox<String> strategyComboBox;
    private JButton startButton;
    private JButton safeButton;
    private JButton brokenButton;
    private JTextArea responseArea;

    public BrickDropInterface() {
        // Set up the main frame
        setTitle("Dropping Bricks Game");
        setSize(1000, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));
        setLocationRelativeTo(null); // Center the frame on the screen


        // Components
        JLabel dropsLabel = new JLabel("Maximum number of Drops -->  ");
        dropsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        numberOfDropsField = new JTextField("0");
        final JPanel dropsPanel = new JPanel(new GridLayout(1, 2));
        dropsPanel.add(dropsLabel);
        dropsPanel.add(numberOfDropsField);

        // Strategy panel
        JLabel strategyLabel = new JLabel("Computer's strategy -->  ");
        strategyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        strategyComboBox = new JComboBox<>(new String[]{"Winning", "Random"});
        final JPanel strategyPanel = new JPanel(new GridLayout(1, 2));
        strategyPanel.add(strategyLabel);
        strategyPanel.add(strategyComboBox);

        // Start button panel
        startButton = new JButton("Start");
        startButton.setMaximumSize(new Dimension(10, 10));
        JPanel startPanel = new JPanel(new GridLayout(1, 1));
        startPanel.add(startButton);

        // Safe or broken button panel
        safeButton = new JButton("Safe");
        safeButton.setEnabled(false);
        brokenButton = new JButton("Broken");
        brokenButton.setEnabled(false);
        final JPanel choicePanel = new JPanel(new GridLayout(1, 2));
        choicePanel.add(safeButton);
        choicePanel.add(brokenButton);

        // Right part - text area
        responseArea = new JTextArea(printIntro(Application.H));
        responseArea.setEditable(false);

        // Compose left half
        JPanel leftPanel = new JPanel(new GridLayout(4, 1));
        leftPanel.add(dropsPanel);
        leftPanel.add(strategyPanel);
        leftPanel.add(startPanel);
        leftPanel.add(choicePanel);

        // Add components to the frame
        add(leftPanel);
        add(responseArea);

        // Action Listeners
        numberOfDropsField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                final char keyChar = e.getKeyChar();
                final String text = numberOfDropsField.getText(); // Validate number from 0 to H
                if (keyChar > '9' || keyChar < '0' || !text.isEmpty() && Integer.parseInt(text + keyChar) > Application.H) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // nothing
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // nothing
            }
        });

        startButton.addActionListener(e -> {
            if (numberOfDropsField.getText().isEmpty()) {
                numberOfDropsField.setText("0");
            }
            final String dropsFieldText = numberOfDropsField.getText();
            final int drops = Integer.parseInt(dropsFieldText);
            String strategy = strategyComboBox.getSelectedIndex() == 0 ? "winning" : "random";
            startButton.setVisible(false);
            numberOfDropsField.setEditable(false);
            strategyComboBox.setEnabled(false);
            responseArea.setText("");

            new Thread(() -> Application.start(this, drops, strategy.equals("random"))).start();
        });

        ActionListener buttonListener = (ActionEvent e) -> {
            safeButton.setEnabled(false);
            brokenButton.setEnabled(false);
            currentStatus = e.getActionCommand().toLowerCase();
        };
        safeButton.addActionListener(buttonListener);
        brokenButton.addActionListener(buttonListener);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BrickDropInterface().setVisible(true));
    }

    private static String printIntro(int height) {
        return """
            Hello my dear friend! 👋
            I welcome you to play the Dropping Bricks Problem game%n
            Here is the description 📝 of the game:
            You need to choose the number 🔢 from 1 to %d and bear it in mind. It will be the strength 💪 of each brick
            My goal is to guess this number by dropping bricks (I have 2 of them)
            Also, you should specify maximum number of such drops and what strategy I should take (random or winning)
            During the game I drop one brick and ask you to reply whether the brick was safe or broken
            I hope you understood the rules. Let's play
            Wish you lots of fun 😁%n%n""".formatted(height);
    }

    /**
     * Displays window with the text and Yes and No buttons finishing the game after it
     * */
    public void displayGuess(String text) {

        final int result = JOptionPane.showConfirmDialog(this,
            text, "Guess", JOptionPane.YES_NO_OPTION);

        // Finish the game - prompt the user to know whether the agent's guess was right and celebrate!
        text = "Thank you very much for the game. I was pleased to play with you! ♥";
        if (result == JOptionPane.YES_OPTION) {
            text = "Wohoo. I won!!!\n" + text;
        } else {
            text = "Ohh. Bad luck. Ok, I'll do it next time\n" + text;
        }
        JOptionPane.showMessageDialog(this, text, "Thanks!", 1);
        dispose();
    }

    /**
     * Appends text to the response area. Clears first when the area is almost full
     */
    public void addText(String text) {
        if (responseArea.getText().length() > 650) {
            responseArea.setText("");
        }
        responseArea.setText("""
            %s
            %s""".formatted(responseArea.getText(), text));
    }

    /**
     * Enables buttons for voting 'safe' or 'broken'
     */
    public void enableButtons() {
        safeButton.setEnabled(true);
        brokenButton.setEnabled(true);
    }
}
