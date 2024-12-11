import java.util.List;

public class Parser {

    public List<Token> tokens;
    private int currentTokenIndex;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    private void match(String expectedType) throws Exception {
        //System.out.println("we reached match");

        Token token= currentToken();
        if (token == null) {
            System.out.println("End of input reached. Exiting match method.");// Exit without incrementing currentTokenIndex
            throw new Exception("Syntax Error: Unexpected end of input. Expected " + expectedType);
        }
        if (currentTokenIs(expectedType)) {
            currentTokenIndex++;
        } else {
            throw new Exception("Syntax Error: Expected " + expectedType + " but found " + currentToken().getTokenType());
        }
        System.out.println(currentTokenIndex);
        System.out.println(expectedType);
    }



    private boolean currentTokenIs(String type) {
        return currentTokenIndex <= (tokens.size()-1) && currentToken().getTokenType().equals(type);
        //stop at tokens.size()-1 as counter is 0 indexed
    }

    private Token currentToken() {
        if (currentTokenIndex <= (tokens.size()-1)) { //stop at tokens.size()-1 as counter is 0 indexed
           // System.out.println(currentTokenIndex);
            //System.out.println(tokens.size());
            return tokens.get(currentTokenIndex);
        }
        return null;
    }


    // Grammar rules

    public CustomTreeNode parse() throws Exception {
        return program();
    }

    // Program → stmt-sequence
    private CustomTreeNode program() throws Exception {
        return statementSequence(null);
    }

    // Stmt-sequence → stmt {; stmt}
    CustomTreeNode statementSequence(CustomTreeNode src) throws Exception {
        CustomTreeNode seqNode= new CustomTreeNode("stmt-sequence", "rectangle");
        seqNode= stmt(seqNode);
        if(src != null) src.add(seqNode);
        else src = seqNode;
        while (currentTokenIs("SEMICOLON")) {
            match("SEMICOLON");
            CustomTreeNode seqNode2= new CustomTreeNode("stmt","rectangle");
            seqNode2 = stmt(seqNode2);
            seqNode.addSibling(seqNode2);
            seqNode = seqNode2;
        }
        return src;
    }

    // Stmt → if-stmt | repeat-stmt | assign-stmt | read-stmt | write-stmt
    CustomTreeNode stmt(CustomTreeNode seqNode) throws Exception {
        switch (currentToken().getTokenType()) {
            case "IF":
                return ifStmt(seqNode);
            case "REPEAT":
                return repeatStmt(seqNode);
            case "IDENTIFIER":
                return assignStmt(seqNode);
            case "READ":
                return readStmt(seqNode);
            case "WRITE":
                return writeStmt(seqNode);
            default:
                throw new Exception("Syntax Error: Unexpected statement token " + currentToken().getTokenType());
        }
    }

    // If-stmt → if expr then stmt-sequence [else stmt-sequence] end
    CustomTreeNode ifStmt(CustomTreeNode src) throws Exception {
        src.setUserObject("if");

        match("IF");
        CustomTreeNode conditionNode = expr();
        src.add(conditionNode);
        match("THEN");
        CustomTreeNode thenNode = null;
        thenNode= (statementSequence(thenNode));
        src.add(thenNode);
        if (currentTokenIs("ELSE")) {
            match("ELSE");
            CustomTreeNode elseNode = new CustomTreeNode("else", "rectangle");
            elseNode= statementSequence(elseNode);
            src.add(elseNode);
        }
        match("END");

        return src;
    }

    // Repeat-stmt → repeat stmt-sequence until expr
    CustomTreeNode repeatStmt(CustomTreeNode src) throws Exception {
        src.setUserObject("repeat");
        CustomTreeNode repeatNode = new CustomTreeNode("REPEAT", "rectangle");

        match("REPEAT");
        repeatNode = statementSequence(repeatNode);
        src.add(repeatNode);
        match("UNTIL");
        CustomTreeNode conditionNode =expr();
        src.add(conditionNode);
        return repeatNode;
    }

    // Assign-stmt → identifier := expr
    CustomTreeNode assignStmt(CustomTreeNode src) throws Exception {
        src.setUserObject("assign");
        if(currentTokenIs("IDENTIFIER")) {
            if(currentToken() != null){
                String val = currentToken().getTokenVal();
                src.setUserObject("assign (" + val + ")");
            }
        }
        match("IDENTIFIER");
        match("ASSIGN");
        CustomTreeNode expNode= expr();
        src.add(expNode);
        return src;
    }

    // Read-stmt → read identifier
    CustomTreeNode readStmt(CustomTreeNode src) throws Exception {
        match("READ");
        if(currentTokenIs("IDENTIFIER") && currentToken() != null )
            src.setUserObject("read (" + currentToken().getTokenVal() + ")");
        match("IDENTIFIER");
        return src;
    }

    // Write-stmt → write expr
    CustomTreeNode writeStmt(CustomTreeNode src) throws Exception {
        src.setUserObject("write");
        match("WRITE");
        src.add(expr());
       return src;
    }

    // Expr → simple-exp [comparison-op simple-exp]
    CustomTreeNode expr() throws Exception {
        CustomTreeNode expNode = simpleExp();
       // expNode.add(simpleExp());
        if (currentToken() != null &&
                (currentTokenIs("LESSTHAN") || currentTokenIs("EQUAL"))) {
            CustomTreeNode comparisonNode = new CustomTreeNode("op (" + currentToken().getTokenVal() + ")","circle");
            match(currentToken().getTokenType()); // Match comparison operator
            comparisonNode.add(expNode);
            comparisonNode.add(simpleExp());
            expNode = comparisonNode;
        }
        return expNode;
    }

    // Simple-exp → term {add-op term}
    CustomTreeNode simpleExp() throws Exception {
        CustomTreeNode termNode = term();
        while (currentToken() != null &&
                (currentTokenIs("PLUS") || currentTokenIs("MINUS"))) {
            CustomTreeNode opNode = new CustomTreeNode("op (" + currentToken().getTokenVal() + ")", "circle");
            match(currentToken().getTokenType()); // Match addition operator
            opNode.add(termNode);
            opNode.add(term());
            termNode= opNode;
        }
        return  termNode;
    }

    // Term → factor {mul-op factor}
    CustomTreeNode term() throws Exception {
        CustomTreeNode termNode = factor();

        while (currentToken() != null &&
                (currentTokenIs("MULT") || currentTokenIs("DIV"))) {
            CustomTreeNode mulop = new CustomTreeNode("op (" + currentToken().getTokenVal() + ")", "circle");
            match(currentToken().getTokenType()); // Match multiplication operator
            if(currentToken() != null) {
                mulop.add(termNode);
                mulop.add(factor());
                termNode = mulop;
            } else {
                System.err.println("Parsing error: Missing mul-op");
            }
        }
        return  termNode;
    }

    // Factor → (expr) | number | identifier
    private CustomTreeNode factor() throws Exception {
        if (currentTokenIs("OPENBRACKET")) {
            match("OPENBRACKET");
            CustomTreeNode factorNode = expr();
            match("CLOSEDBRACKET");

            return factorNode;
        } else if (currentTokenIs("NUMBER")) {
            String val = currentToken().getTokenVal();
            match("NUMBER");
            return new CustomTreeNode("const (" + val + ")", "circle");
        } else if (currentTokenIs("IDENTIFIER")) {
            String val = currentToken().getTokenVal();
            match("IDENTIFIER");
            return new CustomTreeNode("id (" + val + ")", "circle");
        } else {
            throw new Exception("Syntax Error: Unexpected factor token " + currentToken().getTokenType());
        }
    }

}
