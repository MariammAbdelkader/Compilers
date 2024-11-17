public class Token {
    private String tokenType;
    private String tokenVal;

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

