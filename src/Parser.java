import java.util.List;

public class Parser {

    private List<Token> tokens;
    private int currentTokenIndex;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    private void match(String expectedType) throws Exception {
        if (currentTokenIs(expectedType)) {
            currentTokenIndex++;
        } else {
            throw new Exception("Syntax Error: Expected " + expectedType + " but found " + currentToken().getTokenType());
        }
    }

    private boolean currentTokenIs(String type) {
        return currentTokenIndex < tokens.size() && currentToken().getTokenType().equals(type);
    }

    private Token currentToken() {
        return currentTokenIndex < tokens.size() ? tokens.get(currentTokenIndex) : null;
    }

    // Grammar rules

    public void parse() throws Exception {
        program();
    }

    // Program → stmt-sequence
    private void program() throws Exception {
        statementSequence();
    }

    // Stmt-sequence → stmt {; stmt}
    private void statementSequence() throws Exception {
        stmt();
        while (currentTokenIs("SEMICOLON")) {
            match("SEMICOLON");
            stmt();
        }
    }

    // Stmt → if-stmt | repeat-stmt | assign-stmt | read-stmt | write-stmt
    private void stmt() throws Exception {
        switch (currentToken().getTokenType()) {
            case "IF":
                ifStmt();
                break;
            case "REPEAT":
                repeatStmt();
                break;
            case "IDENTIFIER":
                assignStmt();
                break;
            case "READ":
                readStmt();
                break;
            case "WRITE":
                writeStmt();
                break;
            default:
                throw new Exception("Syntax Error: Unexpected statement token " + currentToken().getTokenType());
        }
    }

    // If-stmt → if expr then stmt-sequence [else stmt-sequence] end
    private void ifStmt() throws Exception {
        match("IF");
        expr();
        match("THEN");
        statementSequence();
        if (currentTokenIs("ELSE")) {
            match("ELSE");
            statementSequence();
        }
        match("END");
    }

    // Repeat-stmt → repeat stmt-sequence until expr
    private void repeatStmt() throws Exception {
        match("REPEAT");
        statementSequence();
        match("UNTIL");
        expr();
    }

    // Assign-stmt → identifier := expr
    private void assignStmt() throws Exception {
        match("IDENTIFIER");
        match("ASSIGN");
        expr();
    }

    // Read-stmt → read identifier
    private void readStmt() throws Exception {
        match("READ");
        match("IDENTIFIER");
    }

    // Write-stmt → write expr
    private void writeStmt() throws Exception {
        match("WRITE");
        expr();
    }

}
