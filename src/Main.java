

public class Main {

    public static void main(String[] args) {

        LexicalAnalyzer lex = new LexicalAnalyzer();

        lex.readFile("input5.txt");

        SyntaxAnalyzer syn = new SyntaxAnalyzer(lex.gettokenStack());

        System.out.println(syn.analyze());
    }
}