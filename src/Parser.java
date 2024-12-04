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

    // Expr → simple-exp [comparison-op simple-exp]
    private void expr() throws Exception {
        simpleExp();
        if (currentToken() != null &&
                (currentTokenIs("LESSTHAN") || currentTokenIs("EQUAL"))) {
            match(currentToken().getTokenType()); // Match comparison operator
            simpleExp();
        }
    }

    // Simple-exp → term {add-op term}
    private void simpleExp() throws Exception {
        term();
        while (currentToken() != null &&
                (currentTokenIs("PLUS") || currentTokenIs("MINUS"))) {
            match(currentToken().getTokenType()); // Match addition operator
            term();
        }
    }

    // Term → factor {mul-op factor}
    private void term() throws Exception {
        factor();
        while (currentToken() != null &&
                (currentTokenIs("MULT") || currentTokenIs("DIV"))) {
            match(currentToken().getTokenType()); // Match multiplication operator
            factor();
        }
    }

    // Factor → (expr) | number | identifier
    private void factor() throws Exception {
        if (currentTokenIs("OPENBRACKET")) {
            match("OPENBRACKET");
            expr();
            match("CLOSEDBRACKET");
        } else if (currentTokenIs("NUMBER")) {
            match("NUMBER");
        } else if (currentTokenIs("IDENTIFIER")) {
            match("IDENTIFIER");
        } else {
            throw new Exception("Syntax Error: Unexpected factor token " + currentToken().getTokenType());
        }
    }

}
