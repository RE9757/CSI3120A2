import java.util.Stack;

public class SyntaxAnalyzer {

    private Stack<Integer> tokenStack;

    private Stack<Integer> tokenHistory;
    private Stack<Integer> braceStack;

    private static final int EOF = -1;
    private static final int LETTER = 0;
    private static final int DIGIT = 1;
    private static final int UNDERSCORE = 2;
    private static final int UNKNOWN = 99;

    /* Token codes */
    private static final int INT_LIT = 10;
    private static final int FLOAT_LIT = 12;
    private static final int IDENT = 11;
    private static final int STR_LIT = 13;
    private static final int ASSIGN_OP = 20;
    private static final int ADD_OP = 21;
    private static final int SUB_OP = 22;
    private static final int MULT_OP = 23;
    private static final int DIV_OP = 24;
    private static final int LEFT_PAREN = 25;
    private static final int RIGHT_PAREN = 26;
    private static final int LEFT_BRACE = 27;
    private static final int RIGHT_BRACE = 28;
    private static final int SEMICOLON = 29;
    private static final int LESS_THAN = 30;
    private static final int GREATER_THAN = 31;
    private static final int EQUALS = 32;
    private static final int NOT_EQUALS = 33;
    private static final int AND_OP = 34;
    private static final int OR_OP = 35;
    private static final int IF = 36;
    private static final int ELSE = 37;
    private static final int FOR = 38;
    private static final int WHILE = 39;
    private static final int COMMENT = 40;
    private static final int QUESTION_MARK = 41;
    private static final int COLON = 42;

    private static final int BLOCK_COMMENT = 43;//new added for block comment

    private static final int unclosedStringLiteral = 44;//used to make sure whether unclosed String literal happens
    private int lineNumber = 0;

    public SyntaxAnalyzer(Stack tokenStack) {
        this.tokenStack = tokenStack;
        this.tokenHistory = new Stack<>(); // Initialize tokenHistory
        this.braceStack = new Stack<>(); // Initialize braceStack
    }

    public String analyze() {
        String result = "";

        boolean additionFlag = false;

        Integer additionType = 0;

        while (!tokenStack.isEmpty()) {
            int token = tokenStack.pop();

            // Increment lineNumber for specific tokens, assuming they are followed by newlines.
            if(token == COMMENT || token == FOR || token == SEMICOLON || token == WHILE || token == IF
                    || token == RIGHT_BRACE){
                lineNumber++;
            }


            switch (token) {
                case FOR:
                    // Check if tokenHistory is not empty and then peek.
                    if(!tokenHistory.isEmpty() &&
                            (tokenHistory.peek() != SEMICOLON && tokenHistory.peek() != RIGHT_BRACE)){
                        result += "Syntax analysis failed." + "\nsyntax_analyzer_error - Missing semi colon at line " + lineNumber;//input.txt ?
                    } else if (tokenStack.peek()!=LEFT_PAREN) {
                        if(result ==""){
                            result += "Syntax analysis failed.\n" +
                                    "syntax_analyzer_error - Missing '(' at line " + lineNumber;//{} balance for input13.txt
                        }
                    }
                    break;
                case IF:
                    if(tokenStack.peek()!=LEFT_PAREN){
                        result += "Syntax analysis failed.\nsyntax_analyzer_error - Missing '(' at line " + lineNumber;//() balance, input9
                    }
                    break;
                case WHILE:
                    if(tokenStack.peek()!=LEFT_PAREN){
                        result += "Syntax analysis failed.\nsyntax_analyzer_error - Missing '(' at line " + lineNumber;//() balance, input9
                    }
                    break;
                case LEFT_BRACE:
                    braceStack.push(token);
                    break;
                case RIGHT_BRACE:
                    if (braceStack.isEmpty() || braceStack.peek() != LEFT_BRACE ) {
                        if(result ==""){
                            result += "Syntax analysis failed.\nsyntax_analyzer_error - Unmatched closing } at line " + (lineNumber+1);//{} balance for input2.txt
                        }
                    } else {
                        braceStack.pop();
                    }
                    break;
                case LEFT_PAREN:
                    braceStack.push(token);
                    break;
                case RIGHT_PAREN:
                    if (braceStack.isEmpty() || braceStack.peek() != LEFT_PAREN) {
                        if(result =="") {
                            result += "Syntax analysis failed.\n" +
                                    "syntax_analyzer_error - Unmatched closing ) at line " + lineNumber;//() balance, input5
                        }
                    } else {
                        braceStack.pop();
                    }
                    break;
                case MULT_OP:
                    if (tokenHistory.peek() == ADD_OP || tokenHistory.peek() == SUB_OP ||
                            tokenHistory.peek() == MULT_OP || tokenHistory.peek() == DIV_OP){
                        result += "Syntax analysis failed.\nsyntax_analyzer_error - Missing operand before operator at line " + (lineNumber+1);// +* conflict input3.txt
                    }
                    break;
                case BLOCK_COMMENT:
                    lineNumber+=4;// input5
                    break;
                case STR_LIT:
                    if (tokenHistory.peek() == STR_LIT){
                        result += "Syntax analysis failed.\nsyntax_analyzer_error - Missing semi colon at line " + (lineNumber+1);// +* conflict input7.txt
                    } else if(tokenHistory.peek() == IDENT){
                        result += "Syntax analysis failed.\n-syntax_analyzer_error - String assignment error at line " + (lineNumber+1);
                    } else if (tokenHistory.peek() == ADD_OP && additionType!= STR_LIT) {
                        result += "Syntax analysis failed.\n" +
                                "syntax_analyzer_error - String assignment error at line "+(lineNumber+1);//input19
                    }
                    break;
                case INT_LIT:
                    if (tokenHistory.peek() == INT_LIT && result == ""){
                        result += "Syntax analysis failed.\nsyntax_analyzer_error - Missing semi colon at line " + (lineNumber+1);// number conflict input8.txt
                    } else if (tokenHistory.peek() == ADD_OP) {
                        additionFlag = true;
                    }
                    break;
                case ADD_OP:
                    if (additionFlag){
                        result += "Syntax analysis failed.\nsyntax_analyzer_error - Missing semi colon at line " + (lineNumber+1);
                    } else {
                        additionType = tokenHistory.peek();
                    }
                    break;
                case SEMICOLON:
                    additionFlag = false;
                    additionType = 0;
                    break;
                case unclosedStringLiteral:
                    result += "Error - unclosed string literal\n" +
                            "Syntax analysis failed.";
                    break;
                case EOF:
                    if (!braceStack.isEmpty()){
                        if((braceStack.peek() == LEFT_BRACE) && result == ""){
                        result += "Syntax analysis failed.\nsyntax_analyzer_error - Missing '}' at line " + (lineNumber + 1);
                        }
                    }
            }



            tokenHistory.push(token);
        }

        if(result == ""){
            result += "Syntax analysis succeed";
        }

        return result;
    }

}
