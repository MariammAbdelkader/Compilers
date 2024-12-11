import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


    public class Main {
        public static void Program (String input){

            List<String> lines = Arrays.asList(input.split("\\R"));

            // Specify the output file path
            String outputFilePath = "tokens_output.txt";
            try {
                List<Token> tokens = TinyLanguageLexer.tokenize(String.join("\n", lines));
                // Write tokens to a file
                for (Token token : tokens) {
                    System.out.println(token.getTokenVal() + " , " + token.getTokenType() + "\n");
                }
                try (FileWriter writer = new FileWriter(outputFilePath)) {
                    for (Token token : tokens) {
                        writer.write(token.getTokenVal() + " , " + token.getTokenType() + "\n");
                    }
                }

                System.out.println("Tokenization complete. Results written to: " + outputFilePath);
                Parser parser = new Parser(tokens);
                try {
                    CustomTreeNode root = parser.parse();
                    System.out.println("Parsing complete.");

                    // Create the tree visualization app
                    TreeVisualizationApp app = new TreeVisualizationApp(root);

                    // Wrap the app in a JScrollPane
                    JScrollPane scrollPane = new JScrollPane(app);

                    // Enable horizontal and vertical scrolling
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

                    // Create and configure the frame
                    JFrame frame = new JFrame("Custom Tree Visualization");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close this frame only
                    frame.setSize(800, 600); // Adjust initial size
                    frame.setLocationRelativeTo(null); // Center the frame on the screen
                    frame.add(scrollPane); // Add the scroll pane to the frame
                    frame.setVisible(true);
                    frame.setResizable(true);

                    System.out.println("Visualization complete.");

                } catch (Exception e) {
                    System.err.println("Error in parsing: " + e.getMessage());
                }
            } catch (Exception e) {
                try (FileWriter writer = new FileWriter(outputFilePath)) {
                    // Clear the output file
                } catch (IOException ioException) {
                    System.err.println("Error clearing the output file: " + ioException.getMessage());
                }
            }
        }
            public static void main(String[] args) {
            // Create the main frame
            JFrame mainframe = new JFrame("Parser Main Window");
            mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainframe.setSize(800, 600);
            mainframe.setLocationRelativeTo(null); // Center the main frame
            mainframe.setLayout(new BorderLayout()); // Use BorderLayout for better scaling

            // Create a panel for input and add padding
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BorderLayout(10, 10));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Create a label for instructions
            JLabel label = new JLabel("Please Enter your TINY language code:");
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setHorizontalAlignment(SwingConstants.CENTER);

            // Create a scrollable text area for user input
            JTextArea textArea = new JTextArea(15, 30);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);

            // Add components to the input panel
            inputPanel.add(label, BorderLayout.NORTH);
            inputPanel.add(scrollPane, BorderLayout.CENTER);

            // Create a panel for buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

            // Define buttons
            JButton loadFileButton = new JButton("Load File");
            JButton scanButton = new JButton("Scan");
            JButton parseButton = new JButton("Parse");
            JButton clearButton = new JButton("Clear Input");

            // Set button styles
            Font buttonFont = new Font("Arial", Font.BOLD, 14);
            loadFileButton.setFont(buttonFont);
            scanButton.setFont(buttonFont);
            parseButton.setFont(buttonFont);
            clearButton.setFont(buttonFont);

            // Add buttons to the panel
            buttonPanel.add(loadFileButton);
            buttonPanel.add(scanButton);
            buttonPanel.add(parseButton);
            buttonPanel.add(clearButton);

            // Add input panel and button panel to the frame
            mainframe.add(inputPanel, BorderLayout.CENTER);
            mainframe.add(buttonPanel, BorderLayout.SOUTH);

            // Set background color
            mainframe.getContentPane().setBackground(Color.LIGHT_GRAY);

            // Make the frame visible
            mainframe.setVisible(true);

            // Action listeners for buttons
            loadFileButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(mainframe);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                        textArea.setText("");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            textArea.append(line + "\n");
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(mainframe, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            scanButton.addActionListener(e -> {
                String input = textArea.getText();

                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(mainframe, "The input is empty. Please enter your TINY language code.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    List<Token> tokens = TinyLanguageLexer.tokenize(input);
                    StringBuilder tokenDetails = new StringBuilder();

                    for (Token token : tokens) {
                        tokenDetails.append(token.getTokenVal()).append(" : ").append(token.getTokenType()).append("\n");
                    }

                    JOptionPane.showMessageDialog(mainframe, "Scan completed! Tokens:\n" + tokenDetails, "Scan Results", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainframe, "Error during scanning: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            parseButton.addActionListener(e -> {
                String input = textArea.getText();

                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(mainframe, "The input is empty. Please enter your TINY language code.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Call the Program method (implement the parsing logic)
                Program(input);

                // JOptionPane.showMessageDialog(mainframe, "Parsing completed successfully!", "Parse Results", JOptionPane.INFORMATION_MESSAGE);
            });

            clearButton.addActionListener(e -> textArea.setText(""));
        }




    }
