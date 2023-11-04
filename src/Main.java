public class Main {

    public static void main(String[] args) {
        // Check if a filename has been provided as an argument
        if (args.length < 1) {
            System.out.println("Usage: java Main <filename>");
            return; // Exit if no filename is provided
        }

        // Use the provided filename from the command line
        String filename = args[0];

        LexicalAnalyzer lex = new LexicalAnalyzer();

        // Pass the filename to the readFile method
        lex.readFile(filename);

        SyntaxAnalyzer syn = new SyntaxAnalyzer(lex.gettokenStack());

        // Output the result of the analysis
        if(filename!=""){System.out.println(syn.analyze());}
    }
}
