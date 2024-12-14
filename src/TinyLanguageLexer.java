import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TinyLanguageLexer {

    // Token definitions
    private static final Map<String, String> RESERVED_WORDS = Map.ofEntries(
            Map.entry("if", "IF"),
            Map.entry("then", "THEN"),
            Map.entry("end", "END"),
            Map.entry("repeat", "REPEAT"),
            Map.entry("until", "UNTIL"),
            Map.entry("read", "READ"),
            Map.entry("write", "WRITE"),
            Map.entry("else", "ELSE") // Added else keyword
    );

    private static final Map<String, String> SYMBOLS = Map.of(
            ";", "SEMICOLON", ":=", "ASSIGN", "<", "LESSTHAN", "=",
            "EQUAL", "+", "PLUS", "-", "MINUS", "*", "MULT", "/", "DIV",
            "(", "OPENBRACKET", ")", "CLOSEDBRACKET"
    );


    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\s*(\\d+|[a-zA-Z]+|:=|[;<>+=\\-*/()])\\s*"
    );

    public static List<Token> tokenize(String input) throws Exception {
        // Remove comments
        input = input.replaceAll("\\{[^}]*}", ""); // Remove comments enclosed in {}

        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);

        while (matcher.find()) {
            String tokenValue = matcher.group().trim();
            String tokenType;

            if (tokenValue.isEmpty()) continue;

            if (RESERVED_WORDS.containsKey(tokenValue)) {
                tokenType = RESERVED_WORDS.get(tokenValue);
            } else if (SYMBOLS.containsKey(tokenValue)) {
                tokenType = SYMBOLS.get(tokenValue);
            } else if (tokenValue.matches("\\d+")) {
                tokenType = "NUMBER";
            } else if (tokenValue.matches("[a-zA-Z]+")) {
                tokenType = "IDENTIFIER";
            } else {
                throw new Exception("Invalid token: " + tokenValue);
            }

            tokens.add(new Token(tokenType, tokenValue));
        }

        return tokens;
    }

}
