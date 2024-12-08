import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public class Parser {

    public List<Token> tokens;
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

    public DefaultMutableTreeNode parse() throws Exception {
        return program();
    }

    // Program → stmt-sequence
    private DefaultMutableTreeNode program() throws Exception {
        return statementSequence();
    }

    // Stmt-sequence → stmt {; stmt}
    DefaultMutableTreeNode statementSequence() throws Exception {
        DefaultMutableTreeNode seqNode= new DefaultMutableTreeNode("stmt-sequence");
        seqNode.add(stmt());
        while (currentTokenIs("SEMICOLON")) {
            match("SEMICOLON");
            seqNode.add(stmt());
        }
        return seqNode;
    }

    // Stmt → if-stmt | repeat-stmt | assign-stmt | read-stmt | write-stmt
     DefaultMutableTreeNode stmt() throws Exception {
        switch (currentToken().getTokenType()) {
            case "IF":
                return ifStmt();
            case "REPEAT":
                return repeatStmt();
            case "IDENTIFIER":
                return assignStmt();
            case "READ":
                return readStmt();
            case "WRITE":
                return writeStmt();
            default:
                throw new Exception("Syntax Error: Unexpected statement token " + currentToken().getTokenType());
        }
    }

    // If-stmt → if expr then stmt-sequence [else stmt-sequence] end
    DefaultMutableTreeNode ifStmt() throws Exception {
        DefaultMutableTreeNode ifNode = new DefaultMutableTreeNode("IF");

        match("IF");
        DefaultMutableTreeNode conditionNode = expr();
        ifNode.add(conditionNode);
        match("THEN");
        DefaultMutableTreeNode thenNode = statementSequence();
        ifNode.add(thenNode);
        if (currentTokenIs("ELSE")) {
            match("ELSE");
            DefaultMutableTreeNode elseNode= statementSequence();
            ifNode.add(elseNode);
        }
        match("END");

        return ifNode;
    }

    // Repeat-stmt → repeat stmt-sequence until expr
     DefaultMutableTreeNode repeatStmt() throws Exception {
        DefaultMutableTreeNode repeatNode = new DefaultMutableTreeNode("REPEAT");

        match("REPEAT");
        DefaultMutableTreeNode bodyNode = statementSequence();
        repeatNode.add(bodyNode);
        match("UNTIL");
        DefaultMutableTreeNode conditionNode =expr();
        repeatNode.add(conditionNode);
        return repeatNode;
    }

    // Assign-stmt → identifier := expr
    DefaultMutableTreeNode assignStmt() throws Exception {
        DefaultMutableTreeNode assignNode = new DefaultMutableTreeNode("ASSIGN_STMT");

        match("IDENTIFIER");
        DefaultMutableTreeNode idNode = new DefaultMutableTreeNode("IDENTIFIER");
        assignNode.add(idNode);
        match("ASSIGN");
        DefaultMutableTreeNode expNode= expr();
        assignNode.add(expNode);
        return assignNode;
    }

    // Read-stmt → read identifier
    DefaultMutableTreeNode readStmt() throws Exception {
        DefaultMutableTreeNode readNode = new DefaultMutableTreeNode("READ");
        match("READ");
        match("IDENTIFIER");
        return readNode;

    }

    // Write-stmt → write expr
    DefaultMutableTreeNode writeStmt() throws Exception {
        DefaultMutableTreeNode writeNode = new DefaultMutableTreeNode("WRITE");
        match("WRITE");
        writeNode.add(expr());
       return writeNode;
    }

    // Expr → simple-exp [comparison-op simple-exp]
    DefaultMutableTreeNode expr() throws Exception {
        DefaultMutableTreeNode expNode = simpleExp();
       // expNode.add(simpleExp());
        if (currentToken() != null &&
                (currentTokenIs("LESSTHAN") || currentTokenIs("EQUAL"))) {
            DefaultMutableTreeNode comparisonNode = new DefaultMutableTreeNode(currentToken().getTokenType());
            match(currentToken().getTokenType()); // Match comparison operator
            comparisonNode.add(expNode);
            comparisonNode.add(simpleExp());
            expNode = comparisonNode;
        }
        return expNode;
    }

    // Simple-exp → term {add-op term}
    DefaultMutableTreeNode simpleExp() throws Exception {
        DefaultMutableTreeNode termNode = term();
        while (currentToken() != null &&
                (currentTokenIs("PLUS") || currentTokenIs("MINUS"))) {
            DefaultMutableTreeNode opNode = new DefaultMutableTreeNode(currentToken().getTokenType());
            match(currentToken().getTokenType()); // Match addition operator
            opNode.add(termNode);
            opNode.add(term());
            termNode= opNode;

        }
        return  termNode;
    }

    // Term → factor {mul-op factor}
    DefaultMutableTreeNode term() throws Exception {
        DefaultMutableTreeNode termNode = factor();

        while (currentToken() != null &&
                (currentTokenIs("MULT") || currentTokenIs("DIV"))) {
            match(currentToken().getTokenType()); // Match multiplication operator
            DefaultMutableTreeNode mulop = new DefaultMutableTreeNode(currentToken().getTokenType());
            mulop.add(termNode);
            mulop.add(factor());
            termNode = mulop;
        }
        return  termNode;
    }

    // Factor → (expr) | number | identifier
    private DefaultMutableTreeNode factor() throws Exception {
        if (currentTokenIs("OPENBRACKET")) {
            DefaultMutableTreeNode factorNode = new DefaultMutableTreeNode("FACTOR");
            DefaultMutableTreeNode leftbracketNode = new DefaultMutableTreeNode("OPENBRACKET");
            match("OPENBRACKET");
            factorNode.add(leftbracketNode);
            factorNode.add(expr());
            DefaultMutableTreeNode rightBracketNode = new DefaultMutableTreeNode("CLOSEDBRACKET");
            match("CLOSEDBRACKET");
            factorNode.add(rightBracketNode);

            return factorNode;
        } else if (currentTokenIs("NUMBER")) {
            match("NUMBER");
            return new DefaultMutableTreeNode("NUMBER");
        } else if (currentTokenIs("IDENTIFIER")) {
            match("IDENTIFIER");
            return new DefaultMutableTreeNode("IDENTIFIER");
        } else {
            throw new Exception("Syntax Error: Unexpected factor token " + currentToken().getTokenType());
        }
    }

}
