import java.util.*;
import java.io.*;

public class TinyLanguageLexer {

    // Token definitions
    private static final Map<String, String> RESERVED_WORDS = Map.of(
            "if", "IF", "then", "THEN", "end", "END", "repeat", "REPEAT",
            "until", "UNTIL", "read", "READ", "write", "WRITE"
    );
    private static final Map<String, String> SYMBOLS = Map.of(
            ";", "SEMICOLON", ":=", "ASSIGN", "<", "LESSTHAN", "=",
            "EQUAL", "+", "PLUS", "-", "MINUS", "*", "MULT", "/", "DIV",
            "(", "OPENBRACKET", ")", "CLOSEDBRACKET"
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your TINY language code:");

        List<String> lines = new ArrayList<>();
        while (true) {
            String line = scanner.nextLine();
            if (line.isBlank()) break;
            lines.add(line);
        }

        // Specify the output file path
        String outputFilePath = "tokens_output.txt";

        try {
            List<Token> tokens = tokenize(String.join("\n", lines));


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
            System.err.println("Error during tokenization: " + e.getMessage());
        }

        // To test getNextToken func
        Token token = Token.getNextToken();

        if (token != null)
        {
            System.out.println("Token Type: " + token.getTokenType());
            System.out.println("Token Value: " + token.getTokenVal());
        }

        if (token != null) {
            Token token2 = Token.getNextToken();
            System.out.println("Token Type: " + token2.getTokenType());
            System.out.println("Token Value: " + token2.getTokenVal());
        }
    }

    public static List<Token> tokenize(String input) throws Exception {
        // Remove comments
        input = input.replaceAll("\\{[^}]*}", ""); // Remove comments enclosed in {}

        List<Token> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(input, ";<>=+-*/() \t\n", true);

        while (tokenizer.hasMoreTokens()) {
            String tokenValue = tokenizer.nextToken().trim();

            if (tokenValue.isEmpty()) continue;

            String tokenType;
            if (RESERVED_WORDS.containsKey(tokenValue)) {
                tokenType = RESERVED_WORDS.get(tokenValue);
            } else if (SYMBOLS.containsKey(tokenValue)) {
                tokenType = SYMBOLS.get(tokenValue);
            } else if (tokenValue.equals(":")) {
                if (tokenizer.hasMoreTokens()) {
                    String next = tokenizer.nextToken().trim();
                    if (next.equals("=")) {
                        tokenValue = ":=";
                        tokenType = "ASSIGN";
                    } else {
                        throw new Exception("Invalid token: ':' without '='");
                    }
                } else {
                    throw new Exception("Invalid token: ':' at end of input");
                }
            } else if (tokenValue.matches("\\d+")) {
                tokenType = "NUMBER";
            } else if (tokenValue.matches("[a-zA-Z]+")) {
                tokenType = "IDENTIFIER";
            } else {
                throw new Exception("Invalid token: " + tokenValue);
            }

            tokens.add(new Token(tokenType, tokenValue));
            Token.addToken (tokenType, tokenValue);
        }

        return tokens;
    }

}
