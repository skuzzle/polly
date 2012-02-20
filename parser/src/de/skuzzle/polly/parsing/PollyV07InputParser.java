package de.skuzzle.polly.parsing;


import de.skuzzle.polly.parsing.tree.Root;
import de.skuzzle.polly.parsing.tree.TreeElement;
import de.skuzzle.polly.parsing.tree.literals.CommandLiteral;


public class PollyV07InputParser extends InputParser {

    public PollyV07InputParser() {
        super();
    }
    

    @Override
    protected TreeElement parse_input() throws ParseException {
        Root root = null;
        
        if (!this.scanner.match(TokenType.POLLY)) {
            return null;
        }
        if (!this.scanner.match(TokenType.QUESTION)) {
            return null;
        }
        if (!this.scanner.match(TokenType.SEPERATOR)) {
            return null;
        }
        
        Token id = this.scanner.lookAhead();
        if (!id.matches(TokenType.IDENTIFIER)) {
            return null;
        }
        this.expect(id.getType());
        root = new Root(new CommandLiteral(id.getStringValue()));
        
        if (this.scanner.lookAhead().matches(TokenType.SEPERATOR)) {
            this.expect(TokenType.SEPERATOR);
            this.parse_signature(root.getParameters());
        }
        
        this.expect(TokenType.EOS);
        return root;
    }
}
