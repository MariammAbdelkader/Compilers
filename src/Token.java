import java.util.ArrayList;
import java.util.List;

public class Token {
    private String tokenType;
    private String tokenVal;

    private static List<Token> tokens = new ArrayList<>();
    private static int currentIndex = 0;

    public Token() {
        tokenVal = "";
        tokenType = "";
    }

    public Token(String type, String val) {
        this.tokenVal = val;
        this.tokenType = type;

    }


    public String getTokenVal() {
        return this.tokenVal;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public static void addToken(String type, String value) {
        tokens.add(new Token(type, value));
    }
    public static Token getNextToken() {
        if (currentIndex < tokens.size()) {
            return tokens.get(currentIndex++);
        }
        return null;
    }

}

