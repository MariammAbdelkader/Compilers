public class Token {
    private String tokenType;  // Change to JavaBean naming convention
    private String tokenVal;   // Change to JavaBean naming convention

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

}

