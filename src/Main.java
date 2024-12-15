import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {
    // Shared variable to store loaded tokens
    private static List<Token> loadedTokens = null;

    public static void Program(List<Token> tokens) {
        Parser parser = new Parser(tokens);
        try {
            CustomTreeNode root = parser.parse();
            System.out.println("Parsing complete.");

            // Create the tree visualization app
            TreeVisualizationApp app = new TreeVisualizationApp(root);

            // Wrap the app in a JScrollPane
            JScrollPane scrollPane = new JScrollPane(app);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            // Create and configure the frame
            JFrame frame = new JFrame("Custom Tree Visualization");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.add(scrollPane);
            frame.setVisible(true);
            frame.setResizable(true);

            System.out.println("Visualization complete.");
        } catch (Exception e) {
            System.err.println("Error in parsing: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Create the main frame
        JFrame mainframe = new JFrame("Parser Main Window");
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.setSize(800, 600);
        mainframe.setLocationRelativeTo(null);
        mainframe.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Please Enter your TINY language code:");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea textArea = new JTextArea(15, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        inputPanel.add(label, BorderLayout.NORTH);
        inputPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton loadFileButton = new JButton("Load Tokens");
        JButton scanButton = new JButton("Scan");
        JButton parseButton = new JButton("Parse");
        JButton clearButton = new JButton("Clear Input");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        loadFileButton.setFont(buttonFont);
        scanButton.setFont(buttonFont);
        parseButton.setFont(buttonFont);
        clearButton.setFont(buttonFont);

        buttonPanel.add(loadFileButton);
        buttonPanel.add(scanButton);
        buttonPanel.add(parseButton);
        buttonPanel.add(clearButton);

        mainframe.add(inputPanel, BorderLayout.CENTER);
        mainframe.add(buttonPanel, BorderLayout.SOUTH);
        mainframe.getContentPane().setBackground(Color.LIGHT_GRAY);
        mainframe.setVisible(true);

        // Action listener for the "Load Tokens" button
        loadFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(mainframe);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    textArea.setText("");
                    loadedTokens = new ArrayList<>(); // Initialize the shared token list
                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;

                        // Split safely: ':' not followed by '=' so that the := remains intact
                        String[] parts = line.split(":(?!=)");

                        if (parts.length == 2) {
                            String tokenVal = parts[0].trim();
                            String tokenType = parts[1].trim();

                            // Create a new Token object
                            Token token = new Token(tokenType, tokenVal);

                            // Add the token to the list
                            loadedTokens.add(token);
                        } else {
                            System.err.println("Invalid line format: " + line);
                        }
                    }


                    // Debug print to verify tokens
                    System.out.println("Loaded Tokens: " + loadedTokens);

                    JOptionPane.showMessageDialog(mainframe, "Tokens loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(mainframe, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        scanButton.addActionListener(e -> {
            String input = textArea.getText();
            if (input.isEmpty() && loadedTokens == null) {
                JOptionPane.showMessageDialog(mainframe, "The input is empty. Please enter your TINY language code.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            else if (loadedTokens != null){
                StringBuilder tokenDetails = new StringBuilder();

                for (Token token : loadedTokens) {
                    tokenDetails.append(token.getTokenVal()).append(" : ").append(token.getTokenType()).append("\n");
                }
                JOptionPane.showMessageDialog(mainframe, "Tokens already loaded!\n" + tokenDetails, "Scan Results", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                try {
                    List<Token> tokens = TinyLanguageLexer.tokenize(input);
                    loadedTokens = tokens; // Update shared tokens
                    StringBuilder tokenDetails = new StringBuilder();

                    for (Token token : tokens) {
                        tokenDetails.append(token.getTokenVal()).append(" : ").append(token.getTokenType()).append("\n");
                    }

                    try (FileWriter writer = new FileWriter("tokens_output.txt")) {
                        writer.write(tokenDetails.toString());
                        JOptionPane.showMessageDialog(mainframe, "Tokens saved to tokens_output.txt", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }

                    JOptionPane.showMessageDialog(mainframe, "Scan completed!\n" + tokenDetails, "Scan Results", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainframe, "Error during scanning: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        parseButton.addActionListener(e -> {
            try {
                if (loadedTokens != null && !loadedTokens.isEmpty()) {
                    // Debug print to verify tokens are being passed to Program
                    System.out.println("Passing Tokens to Program: " + loadedTokens);
                    Program(loadedTokens); // Use preloaded tokens
                } else {
                    String input = textArea.getText();
                    if (input.isEmpty()) {
                        JOptionPane.showMessageDialog(mainframe, "The input is empty. Please enter your TINY language code.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    List<Token> tokens = TinyLanguageLexer.tokenize(input);
                    System.out.println("Generated Tokens: " + tokens); // Debug print
                    Program(tokens); // Tokenize input and parse
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // Print full error stack trace for debugging
                JOptionPane.showMessageDialog(mainframe, "Error during parsing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        clearButton.addActionListener(e -> textArea.setText(""));
    }
}
