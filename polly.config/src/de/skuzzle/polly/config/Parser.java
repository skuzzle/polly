package de.skuzzle.polly.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
file           -> (eol? (include | section))* eof
section        -> block_comment? '[' identifier ']' inline_comment? eol value_pair+
value_pair     -> block_comment? identifier '=' value_list inline_comment? line_end+
value_list     -> value (',' value)*
value          -> number
                | boolean
                | string

include        -> '@include' string identifier (',' identifier)*
line_end       -> eol | eof
inline_comment -> '//' any* line_end
block_comment  -> '/*' (any | line_end)* 

 * @author Simon
 *
 */
class Parser {
    

    private Scanner scanner;
    private File source;

    
    
    public Parser(String input) {
        this.scanner = new Scanner(input);
    }
    
    
    
    public Parser(File file) throws IOException {
        this.scanner = new Scanner(new FileInputStream(file));
        this.source = file;
    }
    
    
    
    public Parser(File file, String charset) throws IOException {
        this.scanner = new Scanner(new FileInputStream(file), charset);
        this.source = file;
    }
    
    
    
    public void dispose() {
        this.scanner.dispose();
        this.scanner = null;
    }
    
    
    
    private String expectIdentifier() throws ParseException {
        Token la = this.scanner.lookAhead();
        this.expect(TokenType.IDENTIFIER);
        return la.getStringValue();
    }
    
    
    
    private void expect(TokenType expected) throws ParseException {
        
        if (!this.scanner.match(expected)) {
            Token t = this.scanner.lookAhead();
            
            /*
             * HACK: correct position of missing semicolon
             */
            Span pos = t.getPosition();
            String msg = null;
            if (expected.belongsToPrevious()) {
                Token previous = this.scanner.getPrevious();
                pos = previous.getPosition();
                msg = "Missing '" + expected + "' after " + previous.getTokenType() + 
                        " '" + previous.getStringValue() + "'";
            } else {
                msg = "Unexpected Symbol: " + t.getTokenType() + " '" + 
                        t.getStringValue() + "', expected: " + expected;
            }
            throw new ParseException(msg, pos);
        }
    }
    
    
    
    public ConfigurationFile tryParse() {
        try {
            return this.parse();
        } catch (ParseException e) {
            return null;
        }
    }
    
    
    
    public ConfigurationFile parse() throws ParseException {
        ConfigurationFile config = new ConfigurationFile(this.source);
        
        while (!this.scanner.match(TokenType.EOS)) {
            
            if (this.scanner.match(TokenType.INCLUDE)) {
                Token la = this.scanner.lookAhead();
                this.expect(TokenType.STRING);
                String fileName = la.getStringValue();
                
                File file = new File(this.source.getParentFile(), fileName);
                if (file.equals(this.source)) {
                    throw new ConfigException("Configuration can not include itself");
                }
                
                ConfigurationFile inc = null;
                try {
                    inc = ConfigurationFile.open(file);
                } catch (IOException e) {
                    throw new ParseException("Could not read config file from " + file, 
                        la.getPosition());
                }
                config.addInclude(inc);
                
            } else {
                config.addSection(this.parseSection());
            }
        }
        
        return config;
    }
    
    
    
    private Section parseSection() throws ParseException {
        Comment comment = this.scanner.getLastComment();
        this.expect(TokenType.OPENSQBR);
        String sectionName = this.expectIdentifier();
        this.expect(TokenType.CLOSEDSQBR);
        
        Section section = new Section(comment, sectionName);
        while(this.scanner.lookAhead(TokenType.IDENTIFIER)) {
            this.parseValuePair(section);
        }
        return section;
    }
    
    
    
    private void parseValuePair(Section section) throws ParseException {
        Comment comment = this.scanner.getLastComment();
        String key = this.expectIdentifier();
        this.expect(TokenType.EQ);
        Object value = this.parseValueList();
        
        section.add(new ConfigEntry(comment, key, value));
    }
    
    
    
    private Object parseValueList() throws ParseException {
        Position listStart = this.scanner.getPosition();
        Object o = this.parseValue();
        
        if (!this.scanner.lookAhead(TokenType.COMMA)) {
            return o;
        }
        ArrayList<Object> objects = new ArrayList<Object>();
        objects.add(o);
        while (this.scanner.match(TokenType.COMMA)) {
            
            Object o2 = this.parseValue();
            
            // do a little context checking: lists may only contain elements of the
            // same type
            if (!o.getClass().equals(o2.getClass())) {
                throw new ParseException("Lists may only contain elements of the " +
                		"same type", this.scanner.spanFrom(listStart));
            }
            objects.add(o2);
        }
        return objects;
    }
    
    
    
    private Object parseValue() throws ParseException {
        Token la = this.scanner.lookAhead();
        switch (la.getTokenType()) {
        case INTEGER:
            this.scanner.consume();
            return la.getIntValue();
        case FLOAT:
            this.scanner.consume();
            return la.getDoubleValue();
        case TRUE:
            this.scanner.consume();
            return Boolean.TRUE;
        case FALSE: 
            this.scanner.consume();
            return Boolean.FALSE;
        case STRING:
        case IDENTIFIER:
            this.scanner.consume();
            return la.getStringValue();
        default:
            this.expect(TokenType.VALUE);
            return null;
        }
    }
}