import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KMPVisualizer extends JFrame {
    private JTextField textField;
    private JTextField patternField;
    private JButton searchButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public KMPVisualizer() {
        setTitle("KMP Algorithm Visualization");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Setting up the input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        JLabel textLabel = new JLabel("Text:");
        textLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        inputPanel.add(textLabel, gbc);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        inputPanel.add(textField, gbc);

        JLabel patternLabel = new JLabel("Pattern:");
        patternLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        inputPanel.add(patternLabel, gbc);

        patternField = new JTextField();
        patternField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        inputPanel.add(patternField, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Setting up the table for result visualization
        tableModel = new DefaultTableModel(new Object[]{"Text", "Pattern", "Highlight"}, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Monospaced", Font.PLAIN, 16));
        resultTable.setRowHeight(30);
        resultTable.setDefaultRenderer(Object.class, new ResultTableCellRenderer());
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Setting up the status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        // Setting up the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 16));
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        statusPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);

        // Adding action listeners for buttons
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                String pattern = patternField.getText();

                if (!text.isEmpty() && !pattern.isEmpty()) {
                    search(text, pattern);
                } else {
                    JOptionPane.showMessageDialog(KMPVisualizer.this, "Please enter both text and pattern.");
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setText("");
                patternField.setText("");
                tableModel.setRowCount(0);
                statusLabel.setText(" ");
            }
        });
    }

    private void search(String text, String pattern) {
        int[] lps = computeLPSArray(pattern);
        int textIndex = 0;
        int patternIndex = 0;

        tableModel.setRowCount(0);
        boolean found = false;

        while (textIndex < text.length()) {
            StringBuilder textLine = new StringBuilder(text);
            StringBuilder patternLine = new StringBuilder(" ".repeat(textIndex) + pattern);
            StringBuilder highlightLine = new StringBuilder(" ".repeat(textIndex) + "^".repeat(patternIndex));

            tableModel.addRow(new Object[]{textLine.toString(), patternLine.toString(), highlightLine.toString()});

            if (pattern.charAt(patternIndex) == text.charAt(textIndex)) {
                patternIndex++;
                textIndex++;

                // Update the highlight line as we progress through the pattern
                highlightLine = new StringBuilder(" ".repeat(textIndex - patternIndex) + "^".repeat(patternIndex));
                tableModel.addRow(new Object[]{textLine.toString(), patternLine.toString(), highlightLine.toString()});

                if (patternIndex == pattern.length()) {
                    found = true;
                    statusLabel.setText("Pattern found at index " + (textIndex - pattern.length()));
                    break;
                }
            } else {
                if (patternIndex != 0) {
                    patternIndex = lps[patternIndex - 1];
                } else {
                    textIndex++;
                }
            }
        }

        if (!found) {
            statusLabel.setText("Pattern not found in the text.");
        }
    }

    private int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    private static class ResultTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 2) {
                // Highlight column
                cell.setForeground(Color.RED);
            } else {
                cell.setForeground(Color.BLACK);
            }
            return cell;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                KMPVisualizer visualizer = new KMPVisualizer();
                visualizer.setVisible(true);
            }
        });
    }
}

