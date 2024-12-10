import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


    public class Main {

            public static void main(String[] args) {
            JFrame mainframe = new JFrame("Parser Main Window");
            mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainframe.setSize(1000, 800);
            mainframe.setLocationRelativeTo(null); //center the mainframe


            // Create a panel to hold components
            JPanel panel = new JPanel();
            //panel.setLayout(new BorderLayout());
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setPreferredSize(new Dimension(1, 10)); // Set height to create space
            panel.setPreferredSize(new Dimension(600, 200));

            // Create a text area
            JTextArea textArea = new JTextArea(20, 20);
            textArea.setLineWrap(true); // Enable line wrapping
            textArea.setWrapStyleWord(true);  // Ensures words are not split across lines

            // Wrap the JTextArea in a JScrollPane to make it scrollable
            JScrollPane scrollPane = new JScrollPane(textArea);

            //add a label
            JLabel label = new JLabel("Please Enter your TINY language code");

            label.setFont(new Font("Arial", Font.BOLD, 14)); // Customize font if needed
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Top, left, bottom, right padding
            label.setOpaque(true);
            label.setBackground(new Color(0xcce6ff));


            // Create a button
            JButton button = new JButton("PARSE");
            JButton LoadFileButton = new JButton("Load File");


            LoadFileButton.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5)); // Top, left, bottom, right padding
            button.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5)); // Top, left, bottom, right padding

                panel.add(label); // add label to top of panel
                panel.add(Box.createVerticalStrut(10));  // Add space between label and text area
                panel.add(scrollPane);
                panel.add(Box.createVerticalStrut(10));  // Add space between label and text area
                JPanel buttonPanel= new JPanel();
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS)); // Arrange components horizontally

                buttonPanel.add(LoadFileButton);
                buttonPanel.add(Box.createHorizontalStrut(170));

                buttonPanel.add(button);


                panel.add(buttonPanel);
                mainframe.add(panel);

                mainframe.setLayout(new FlowLayout(FlowLayout.CENTER));

                //mainframe.add(buttonPanel,BorderLayout.PAGE_END);
                //panel.add(outputLabel);

                // Add the panel to the frame
                mainframe.getContentPane().setBackground(Color.GRAY); // Light blue color

                // Make the frame visible
                mainframe.setVisible(true);

                LoadFileButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Create a file chooser
                        JFileChooser fileChooser = new JFileChooser(); //create an instance for fileChooser

                        int returnValue = fileChooser.showOpenDialog(mainframe); //display opendialogReturn = user option

                        if (returnValue == JFileChooser.APPROVE_OPTION) { //if user clicked ok
                            File selectedFile = fileChooser.getSelectedFile();//Retrieve selected file from file chooser
                            if(selectedFile == null || !selectedFile.exists()){
                                JOptionPane.showMessageDialog(new JFrame(), "No file was selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {   // Read the content of the selected file using BufferedReader
                                String line;
                                StringBuilder fileContent = new StringBuilder(); //StringBuilder to accumulate lines
                                while ((line = reader.readLine()) != null) {
                                    fileContent.append(line).append("\n");//append lines to the string builder
                                }
                                // Display the file content in the text area
                                textArea.setText(fileContent.toString());
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(mainframe, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });


            // action listener to the button
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Get the text from the text field
                    String input = textArea.getText();
                    if(input.length() == 0){
                        JOptionPane.showMessageDialog(null,"The input is empty, Please Enter your TINY language code");
                    }
                    // Display the text in the label
                    //outputLabel.setText("You entered: " + inputText);
                }
            });



            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your TINY language code:");

            List<String> lines = new ArrayList<>();
            while (true) {
                String line = scanner.nextLine();
                if (line.isBlank()) break;
                lines.add(line);
            }
            scanner.close();

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

                    TreeVisualizationApp app = new TreeVisualizationApp(root);

                    JFrame frame = new JFrame("Custom Tree Visualization");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(1500, 400);
                    frame.add(app);
                    frame.setVisible(true);
                    System.out.println("visualization complete.");
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
    }
