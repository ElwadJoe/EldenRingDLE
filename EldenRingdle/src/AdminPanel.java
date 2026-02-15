import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class AdminPanel extends JFrame {

    // Table Models for the Database View
    private DefaultTableModel charTableModel;
    private DefaultTableModel quoteTableModel;

    public AdminPanel() {
        setTitle("Elden Ringdle - Admin Console");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        JPanel charPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        charPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameF = new JTextField();
        JTextField genderF = new JTextField();
        JTextField raceF = new JTextField();
        JTextField regionF = new JTextField();
        JTextField affF = new JTextField();
        JTextField typeF = new JTextField();

        setupFieldAutocomplete(raceF, "Race");
        setupFieldAutocomplete(regionF, "Region");
        setupFieldAutocomplete(affF, "Affiliation");
        setupFieldAutocomplete(typeF, "Type");

        charPanel.add(new JLabel("Name:")); charPanel.add(nameF);
        charPanel.add(new JLabel("Gender (Male/Female/None):")); charPanel.add(genderF);
        charPanel.add(new JLabel("Race (e.g. Demigod):")); charPanel.add(raceF);
        charPanel.add(new JLabel("Region (e.g. Caelid):")); charPanel.add(regionF);
        charPanel.add(new JLabel("Affiliation (e.g. Golden Order):")); charPanel.add(affF);
        charPanel.add(new JLabel("Combat Type (Melee/Magic):")); charPanel.add(typeF);

        JButton addCharBtn = new JButton("Add Character");
        addCharBtn.setBackground(new Color(46, 204, 113));
        addCharBtn.setForeground(Color.WHITE);

        addCharBtn.addActionListener(e -> {
            GameData.characterList.add(new GameData.EldenChar(
                    nameF.getText(), genderF.getText(), raceF.getText(),
                    regionF.getText(), affF.getText(), typeF.getText()
            ));
            JOptionPane.showMessageDialog(this, "Character Added!");
            clearFields(nameF, genderF, raceF, regionF, affF, typeF);
            refreshDatabaseTables(); // Update the database tab immediately
        });
        charPanel.add(new JLabel("")); charPanel.add(addCharBtn);

        JPanel quotePanel = new JPanel(new GridLayout(4, 1, 10, 10));
        quotePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField quoteTextF = new JTextField();
        JTextField quoteAuthorF = new JTextField();

        setupFieldAutocomplete(quoteAuthorF, "Name");

        quotePanel.add(new JLabel("Quote Text:"));
        quotePanel.add(quoteTextF);
        quotePanel.add(new JLabel("Character Who Said It:"));
        quotePanel.add(quoteAuthorF);

        JButton addQuoteBtn = new JButton("Add Quote");
        addQuoteBtn.setBackground(new Color(46, 204, 113));
        addQuoteBtn.setForeground(Color.WHITE);

        addQuoteBtn.addActionListener(e -> {
            GameData.quoteList.add(new GameData.Quote(quoteTextF.getText(), quoteAuthorF.getText()));
            JOptionPane.showMessageDialog(this, "Quote Added!");
            quoteTextF.setText(""); quoteAuthorF.setText("");
            refreshDatabaseTables();
        });
        quotePanel.add(addQuoteBtn);

        // Database View
        JPanel dbPanel = new JPanel(new GridLayout(2, 1));

        // Character Table
        JPanel topDb = new JPanel(new BorderLayout());
        topDb.setBorder(BorderFactory.createTitledBorder("Characters Database"));

        String[] charCols = {"Name", "Gender", "Race", "Region", "Affiliation", "Type"};
        charTableModel = new DefaultTableModel(charCols, 0);
        JTable charTable = new JTable(charTableModel);
        JButton deleteCharBtn = new JButton("Delete Selected Character");
        deleteCharBtn.setBackground(new Color(231, 76, 60));
        deleteCharBtn.setForeground(Color.WHITE);

        deleteCharBtn.addActionListener(e -> {
            int row = charTable.getSelectedRow();
            if (row >= 0) {
                GameData.characterList.remove(row);
                refreshDatabaseTables();
            }
        });

        topDb.add(new JScrollPane(charTable), BorderLayout.CENTER);
        topDb.add(deleteCharBtn, BorderLayout.SOUTH);

        // Quote Table
        JPanel botDb = new JPanel(new BorderLayout());
        botDb.setBorder(BorderFactory.createTitledBorder("Quotes Database"));

        String[] quoteCols = {"Quote", "Author"};
        quoteTableModel = new DefaultTableModel(quoteCols, 0);
        JTable quoteTable = new JTable(quoteTableModel);
        JButton deleteQuoteBtn = new JButton("Delete Selected Quote");
        deleteQuoteBtn.setBackground(new Color(231, 76, 60));
        deleteQuoteBtn.setForeground(Color.WHITE);

        deleteQuoteBtn.addActionListener(e -> {
            int row = quoteTable.getSelectedRow();
            if (row >= 0) {
                GameData.quoteList.remove(row);
                refreshDatabaseTables();
            }
        });

        botDb.add(new JScrollPane(quoteTable), BorderLayout.CENTER);
        botDb.add(deleteQuoteBtn, BorderLayout.SOUTH);

        dbPanel.add(topDb);
        dbPanel.add(botDb);

        tabs.addTab("Add Character", charPanel);
        tabs.addTab("Add Quote", quotePanel);
        tabs.addTab("Database View", dbPanel);

        add(tabs, BorderLayout.CENTER);

        refreshDatabaseTables();

        setVisible(true);
    }
    private void refreshDatabaseTables() {
        charTableModel.setRowCount(0);
        for (GameData.EldenChar c : GameData.characterList) {
            charTableModel.addRow(c.toRow());
        }

        quoteTableModel.setRowCount(0);
        for (GameData.Quote q : GameData.quoteList) {
            quoteTableModel.addRow(q.toRow());
        }
    }
    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }
    private void setupFieldAutocomplete(JTextField field, String category) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(new Color(50, 50, 50));

        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }

            void update() {
                SwingUtilities.invokeLater(() -> {
                    String text = field.getText().trim().toLowerCase();
                    popup.setVisible(false);
                    popup.removeAll();

                    if (text.isEmpty()) return;
                    List<String> options = GameData.getUniqueValues(category);

                    for (String opt : options) {
                        if (opt.toLowerCase().startsWith(text)) {
                            JMenuItem item = new JMenuItem(opt);
                            item.setBackground(new Color(50, 50, 50));
                            item.setForeground(Color.WHITE);
                            item.addActionListener(ev -> {
                                field.setText(opt);
                                popup.setVisible(false);
                            });
                            popup.add(item);
                        }
                    }

                    if (popup.getComponentCount() > 0) {
                        popup.show(field, 0, field.getHeight());
                        field.requestFocus();
                    }
                });
            }
        });

        // Add Enter Key Support
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (popup.isVisible() && popup.getComponentCount() > 0) {
                        MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
                        if (path != null && path.length > 0) {
                            Component c = path[path.length - 1].getComponent();
                            if (c instanceof JMenuItem) {
                                ((JMenuItem) c).doClick();
                                return;
                            }
                        }
                        ((JMenuItem) popup.getComponent(0)).doClick();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN && popup.isVisible()) {
                    MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{
                            popup, (MenuElement) popup.getComponent(0)
                    });
                }
            }
        });
    }
}