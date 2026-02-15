import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class EldenRingdle {

    private static GameData.EldenChar targetCharacter;
    private static JFrame frame;
    private static JPanel gridPanel;
    private static JTextField inputField;
    private static JPopupMenu suggestionsPopup;

    // --- 1. NEW MAIN ENTRY POINT (LOGIN) ---
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // Login Dialog
            JFrame loginFrame = new JFrame("Elden Ringdle - Login");
            loginFrame.setSize(300, 200);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLayout(new GridLayout(3, 1, 10, 10));

            JLabel lbl = new JLabel("Welcome, Tarnished.", SwingConstants.CENTER);
            lbl.setFont(new Font("Serif", Font.BOLD, 18));

            JButton userBtn = new JButton("Play Game (User)");
            JButton adminBtn = new JButton("Enter Admin Panel");

            // USER ACTION
            userBtn.addActionListener(e -> {
                loginFrame.dispose(); // Close login
                pickDailyCharacter();
                createAndShowGameGUI(); // Start Game
            });

            // ADMIN ACTION
            adminBtn.addActionListener(e -> {
                String pass = JOptionPane.showInputDialog(loginFrame, "Enter Admin Password:");
                if ("1234".equals(pass)) { // Password
                    new AdminPanel(); // Admin Panel
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid Password");
                }
            });

            loginFrame.add(lbl);
            loginFrame.add(userBtn);
            loginFrame.add(adminBtn);
            loginFrame.setVisible(true);
        });
    }

    private static void pickDailyCharacter() {
        Random rand = new Random();
        targetCharacter = GameData.characterList.get(rand.nextInt(GameData.characterList.size()));
        System.out.println("Debug - Target is: " + targetCharacter.name);
    }

    // Game GUI
    private static void createAndShowGameGUI() {
        Color darkBg = new Color(30, 30, 30);
        Color goldText = new Color(193, 157, 83);

        frame = new JFrame("Elden Ringdle - Offline Edition");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.getContentPane().setBackground(darkBg);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("RINGDLE", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(goldText);
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        frame.add(title, BorderLayout.NORTH);

        gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));
        gridPanel.setBackground(darkBg);

        JPanel headerRow = new JPanel(new GridLayout(1, 6, 10, 10));
        headerRow.setBackground(darkBg);
        headerRow.setMaximumSize(new Dimension(800, 40));
        String[] headers = {"Name", "Gender", "Race", "Region", "Affiliation", "Type"};
        for (String h : headers) {
            JLabel lbl = new JLabel(h, SwingConstants.CENTER);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            headerRow.add(lbl);
        }
        gridPanel.add(headerRow);
        gridPanel.add(Box.createVerticalStrut(10));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.getViewport().setBackground(darkBg);
        scrollPane.setBorder(null);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(darkBg);
        inputPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        inputField.setBackground(new Color(50, 50, 50));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);

        JButton guessBtn = new JButton("GUESS");
        guessBtn.setBackground(goldText);
        guessBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        guessBtn.addActionListener(e -> submitGuess());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(guessBtn, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        setupAutocomplete();
        frame.setVisible(true);
    }

    private static void submitGuess() {
        String text = inputField.getText().trim();
        GameData.EldenChar guess = findCharByName(text);

        if (guess == null) {
            if (!text.isEmpty()) JOptionPane.showMessageDialog(frame, "Character not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (suggestionsPopup != null) suggestionsPopup.setVisible(false);

        addGuessRow(guess);
        inputField.setText("");
        inputField.requestFocus();

        if (guess.name.equals(targetCharacter.name)) {
            JOptionPane.showMessageDialog(frame, "Foul Tarnished... You have won!", "Victory", JOptionPane.INFORMATION_MESSAGE);
            new QuoteGame();
            inputField.setEnabled(false);
        }
    }

    private static void addGuessRow(GameData.EldenChar guess) {
        JPanel row = new JPanel(new GridLayout(1, 6, 5, 5));
        row.setBackground(new Color(30, 30, 30));
        row.setMaximumSize(new Dimension(800, 60));
        row.setBorder(new EmptyBorder(5, 0, 5, 0));

        row.add(createTile(guess.name, guess.name.equals(targetCharacter.name)));
        row.add(createTile(guess.gender, guess.gender.equals(targetCharacter.gender)));
        row.add(createTile(guess.race, guess.race.equals(targetCharacter.race)));
        row.add(createTile(guess.region, guess.region.equals(targetCharacter.region)));
        row.add(createTile(guess.affiliation, guess.affiliation.equals(targetCharacter.affiliation)));
        row.add(createTile(guess.combatType, guess.combatType.equals(targetCharacter.combatType)));

        gridPanel.add(row, 1);
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private static JLabel createTile(String text, boolean match) {
        JLabel tile = new JLabel(text, SwingConstants.CENTER);
        tile.setOpaque(true);
        tile.setFont(new Font("SansSerif", Font.BOLD, 14));
        tile.setForeground(Color.WHITE);
        tile.setBackground(match ? new Color(46, 204, 113) : new Color(231, 76, 60));
        tile.setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20), 2));
        return tile;
    }

    private static GameData.EldenChar findCharByName(String name) {
        for (GameData.EldenChar c : GameData.characterList) {
            if (c.name.equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    private static void setupAutocomplete() {
        suggestionsPopup = new JPopupMenu();
        suggestionsPopup.setBackground(new Color(50, 50, 50));
        suggestionsPopup.setBorder(BorderFactory.createLineBorder(new Color(193, 157, 83)));

        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
                    if (path != null && path.length > 0) {
                        Component c = path[path.length - 1].getComponent();
                        if (c instanceof JMenuItem && suggestionsPopup.isVisible()) {
                            ((JMenuItem) c).doClick();
                            return;
                        }
                    }
                    suggestionsPopup.setVisible(false);
                    submitGuess();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (suggestionsPopup.isVisible() && suggestionsPopup.getComponentCount() > 0) {
                        MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{
                                suggestionsPopup, (MenuElement) suggestionsPopup.getComponent(0)
                        });
                    }
                }
            }
        });
    }

    private static void updateSuggestions() {
        SwingUtilities.invokeLater(() -> {
            String text = inputField.getText().trim().toLowerCase();
            suggestionsPopup.setVisible(false);
            suggestionsPopup.removeAll();
            if (text.isEmpty()) return;
            for (GameData.EldenChar c : GameData.characterList) {
                if (c.name.toLowerCase().startsWith(text)) {
                    JMenuItem item = new JMenuItem(c.name);
                    item.setBackground(new Color(50, 50, 50));
                    item.setForeground(Color.WHITE);
                    item.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    item.addActionListener(e -> {
                        inputField.setText(c.name);
                        suggestionsPopup.setVisible(false);
                        submitGuess();
                    });
                    suggestionsPopup.add(item);
                }
            }
            if (suggestionsPopup.getComponentCount() > 0) {
                suggestionsPopup.show(inputField, 0, -suggestionsPopup.getPreferredSize().height);
                inputField.requestFocus();
            }
        });
    }
}