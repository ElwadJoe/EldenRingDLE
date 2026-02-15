import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuoteGame extends JFrame {

    private JLabel quoteLabel;
    private JLabel feedbackLabel;
    private JPanel buttonPanel;
    private JButton closeButton;
    private String currentAuthor;

    public QuoteGame() {
        setTitle("Bonus Round: Elden Ring Quote");
        setSize(700, 400);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color bgColor = new Color(30, 30, 30);
        Color goldColor = new Color(194, 168, 62);
        Color textColor = new Color(230, 230, 230);

        getContentPane().setBackground(bgColor);

        JLabel title = new JLabel("Who Said It?", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(goldColor);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        quoteLabel = new JLabel("Loading...", SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Serif", Font.ITALIC, 22));
        quoteLabel.setForeground(textColor);
        quoteLabel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(quoteLabel, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(bgColor);

        buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        feedbackLabel.setForeground(goldColor);

        closeButton = new JButton("CLOSE");
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dispose());
        closeButton.setVisible(false);

        bottomContainer.add(buttonPanel, BorderLayout.CENTER);

        JPanel feedbackPanel = new JPanel(new BorderLayout());
        feedbackPanel.setBackground(bgColor);
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 20));
        feedbackPanel.add(feedbackLabel, BorderLayout.CENTER);

        JPanel closeBtnContainer = new JPanel();
        closeBtnContainer.setBackground(bgColor);
        closeBtnContainer.add(closeButton);
        feedbackPanel.add(closeBtnContainer, BorderLayout.EAST);

        bottomContainer.add(feedbackPanel, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);

        loadSingleQuote();
        setVisible(true);
    }

    private void loadSingleQuote() {
        if (GameData.quoteList.isEmpty()) return;

        Random rand = new Random();
        GameData.Quote randomQuote = GameData.quoteList.get(rand.nextInt(GameData.quoteList.size()));

        quoteLabel.setText("<html><center>\"" + randomQuote.text + "\"</center></html>");
        currentAuthor = randomQuote.author;

        ArrayList<String> options = new ArrayList<>();
        options.add(currentAuthor);

        int safetyCount = 0;
        while (options.size() < 4 && safetyCount < 100) {
            String randomWrong = GameData.quoteList.get(rand.nextInt(GameData.quoteList.size())).author;
            if (!options.contains(randomWrong)) {
                options.add(randomWrong);
            }
            safetyCount++;
        }
        Collections.shuffle(options);

        for (String author : options) {
            JButton btn = new JButton(author);
            btn.setFont(new Font("Serif", Font.PLAIN, 16));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(new Color(230, 230, 230));
            btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
            btn.addActionListener(e -> checkAnswer(btn, author));
            buttonPanel.add(btn);
        }
    }

    private void checkAnswer(JButton selectedBtn, String selectedAuthor) {
        for (Component c : buttonPanel.getComponents()) {
            if (c instanceof JButton) ((JButton) c).setEnabled(false);
        }

        if (selectedAuthor.equals(currentAuthor)) {
            selectedBtn.setBackground(new Color(34, 139, 34));
            selectedBtn.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            feedbackLabel.setText("LEGEND FELLED");
            feedbackLabel.setForeground(Color.GREEN);
        } else {
            selectedBtn.setBackground(new Color(139, 0, 0));
            feedbackLabel.setText("YOU DIED. It was " + currentAuthor);
            feedbackLabel.setForeground(Color.RED);

            for (Component c : buttonPanel.getComponents()) {
                JButton b = (JButton) c;
                if (b.getText().equals(currentAuthor)) {
                    b.setBackground(new Color(34, 139, 34));
                    b.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                }
            }
        }
        closeButton.setVisible(true);
    }
}