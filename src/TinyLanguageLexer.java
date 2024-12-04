import java.util.*;

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

    public static List<Token> tokenize(String input) throws Exception {
        // Remove comments
        input = input.replaceAll("\\{[^}]*}", ""); // Remove comments enclosed in {}

        List<Token> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(input, "[a-zA-Z]+|\\d+|[;<>=+-*/() \t\n]", true);

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
