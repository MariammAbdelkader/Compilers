import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                for (Token token : tokens) {
                    writer.write(token.getTokenVal() + " , " + token.getTokenType() + "\n");
                }
            }

            System.out.println("Tokenization complete. Results written to: " + outputFilePath);
        } catch (Exception e) {
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                // Clear the output file
            }
            catch (IOException ioException) {
                System.err.println("Error clearing the output file: " + ioException.getMessage());
            }
        }
    }
}
