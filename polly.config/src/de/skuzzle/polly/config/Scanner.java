package de.skuzzle.polly.config;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



class Scanner {
    
    private final static boolean debug = false;
    private final static Map<String, TokenType> keywords = new HashMap<String, TokenType>();
    static {
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
    }

    private Token look;
    private Position position;
    private Position[] positionBuffer;
    private char[] stream;
    private boolean eos;
    private List<Token> consumed;
    private Comment lastComment;
    
    
    
    public Scanner(String input) {
        this.stream = input.toCharArray();
        this.position = new Position(1, 1, 0);
        this.positionBuffer = new Position[input.length() + 1];
        this.consumed = new ArrayList<Token>();
    }
    
    
    
    public Scanner(InputStream stream) throws IOException {
        this(stream, "ISO-8859-1");
    }
    
    
    
    public Scanner(InputStream stream, String charset) throws IOException {
        BufferedReader r = null;
        StringBuilder content = new StringBuilder();
        try {
            r = new BufferedReader(new InputStreamReader(stream, 
                    Charset.forName(charset)));
            String line = null;
            while ((line = r.readLine()) != null) {
                content.append(line);
                content.append('\n');
            }
        } finally {
            if (r != null) {
                r.close();
            }
        }
        
        this.stream = content.toString().toCharArray();
        this.position = new Position(1, 1, 0);
        this.positionBuffer = new Position[this.stream.length + 1];
        this.consumed = new ArrayList<Token>();
    }
    
    
    
    public String getOriginal() {
        return new String(this.stream);
    }
    
    
    
    public Comment getLastComment() {
        Comment last = this.lastComment;
        this.lastComment = null;
        return last;
    }
    
    
    
    public void dispose() {
        for (int i = 0; i < this.positionBuffer.length; ++i) {
            this.positionBuffer[i] = null;
        }
        this.consumed.clear();
        this.consumed = null;
        this.positionBuffer = null;
        this.position = null;
        this.stream = null;
        this.look = null;
        this.lastComment = null;
    }
    
    
    
    public void skipWhile(TokenType type) throws ParseException {
        while (this.match(type));
    }

    
    
    public boolean match(TokenType expected) throws ParseException {
        if (this.lookAhead(expected)) {
            this.consume();
            return true;
        }
        return false;
    }

    
    
    public boolean match(Token token) throws ParseException {
        return this.match(token.getTokenType());
    }
    
    

    public Token lookAhead() throws ParseException {
        if (this.look == null) {
            this.look = this.readToken();
        }
        return this.look;
    }
    
    
    
    public boolean lookAhead(TokenType type) throws ParseException {
        return this.lookAhead().matches(type);
    }

    
    
    public void consume() throws ParseException {
        Token t = this.readToken();
        this.consumed.add(t);
        if (debug) {
            System.out.println(t);
        }
    }
    
    
    
    public List<Token> getConsumed() {
        return this.consumed;
    }
    
    
    
    public Token getPrevious() {
        return this.consumed.get(this.consumed.size() - 1);
    }
    
    
    
    private void revert(int n) {
        int newIndex = this.position.getIndex() - n;
        if (newIndex < 0) {
            throw new IllegalArgumentException("cant revert " + n + 
                    " characters from current stream position");
        } else if (n <= 0) {
            throw new IllegalArgumentException("n must be positive and not zero");
        }
        this.eos = false;
        this.position = new Position(this.positionBuffer[newIndex]);
    }
    
    
    
    private char readChar() {
        return this.readChar(false);
    }

    
    
    private char readChar(boolean trackLine) {
        // buffer position data for current stream index
        this.maintainPositionBuffer();
        int next = this.position.getChar(this.stream);
        if (next == '\n') {
            this.position.incrementLine();
        } else {
            this.position.incrementColumn();
        }
        if (next == -1) {
            next = '\0';
            this.eos = true;
        }
        return (char) next;
    }
    
    
    
    private void maintainPositionBuffer() {
        this.positionBuffer[this.position.getIndex()] = new Position(this.position);
    }
    
    
    
    public Position getPosition() {
        return this.position;
    }
    

    
    public Span spanFrom(Position start) {
        return new Span(new Position(start), new Position(this.position));
    }
    
    
    
    
    /** constant for initial state in all following state machines */
    private final static int INITIAL = 0;
    
    
    private Token readToken() throws ParseException {
        // return lookahead token
        if (this.look != null) {
            Token tmp = this.look;
            this.look = null;
            return tmp;
        }
        
        Position start = new Position(this.position);
        while (!this.eos) {
            int next = this.readChar();
            
            switch (next) {
            case '\0':  return new Token(TokenType.EOS, this.spanFrom(start));
            case '\n':
                //this.position.incrementLine();
                //return new Token(TokenType.LINEBREAK, this.spanFrom(start));
            case ' ':
                start = new Position(this.position);
                break;
            case '=': return new Token(TokenType.EQ, this.spanFrom(start));
            case '[': return new Token(TokenType.OPENSQBR, this.spanFrom(start));
            case ']': return new Token(TokenType.CLOSEDSQBR, this.spanFrom(start));
            case '{': return new Token(TokenType.OPENCBR, this.spanFrom(start));
            case '}': return new Token(TokenType.CLOSEDCBR, this.spanFrom(start));
            case ',': return new Token(TokenType.COMMA, this.spanFrom(start));
            case '@':
                Token t = this.readIdentifier(start);
                if (!t.getStringValue().equals("include")) {
                    throw new ParseException("Invalid @include statement: '@" 
                        + t.getStringValue() + "'", 
                        this.spanFrom(start));
                }
                return new Token(TokenType.INCLUDE, this.spanFrom(start));
            case '/': 
                this.readComment(start);
                return this.readToken();
            case '"': return this.readString(start);
            default:
                if (Character.isJavaIdentifierStart((char) next)) {
                    this.revert(1);
                    return this.readIdentifier(start);
                } else if (Character.isDigit(next) || (char)next == '-') {
                    this.revert(1);
                    return this.readNumber(start);
                } else {
                    throw new ParseException("Invalid character: '" + ((char)next) + "'", 
                            this.spanFrom(start));
                }
            }
        }
        
        return new Token(TokenType.EOS, this.spanFrom(start));
    }
    
    
    
    /*
     * States for read comment:
     */
    
    /** we are reading a block comment */
    private final static int BLOCK_COMMENT = 2;

    /** we hit a potential block comment end */
    private final static int BLOCK_COMMENT_END = 3;
    
    /** we read an inline comment */
    private final static int INLINE_COMMENT = 1;
    
    private Token readComment(Position start) throws ParseException {
        StringBuilder comment = new StringBuilder();
        int state = INITIAL;
        
        // by now, we read a '/'
        
        while (!this.eos) {
            // true => comments keep track of line changes themselves
            boolean trackLineBreak = state == BLOCK_COMMENT;
            char next = this.readChar(trackLineBreak);
            
            switch (state) {
            case INITIAL:
                if (next == '/') {
                    state = INLINE_COMMENT;
                } else if (next == '*') {
                    state = BLOCK_COMMENT;
                }
                break;
                
            case INLINE_COMMENT:
                if (next == '\n') {
                    this.revert(1);
                    this.lastComment = new Comment(comment.toString(), false);
                    Token result = new Token(comment.toString(), TokenType.INLINECOMMENT, 
                            this.spanFrom(start));
                    return result;
                } else {
                    comment.append(next);
                }
                break;
                
            case BLOCK_COMMENT:
                if (next == '*') {
                    state = BLOCK_COMMENT_END;
                } else {
                    comment.append(next);
                }
                break;
                
            case BLOCK_COMMENT_END:
                if (next == '/') {
                    this.lastComment = new Comment(comment.toString(), true);
                    return new Token(comment.toString(), TokenType.BLOCKCOMMENT, 
                            this.spanFrom(start));
                } else {
                    comment.append("*");
                    this.revert(1);
                    state = BLOCK_COMMENT;
                }
            }
        }
        
        throw new ParseException("Unclosed Block-Comment", this.spanFrom(start));
    }

    

    /** we have read the first char of an identifier and are now reading the part */
    private final static int PART = 1;

    private Token readIdentifier(Position start) throws ParseException {
        StringBuilder key = new StringBuilder();
        int state = INITIAL;
        
        while (!this.eos) {
            char next = this.readChar(false);
            
            switch (state) {
            case INITIAL:
                if (Character.isJavaIdentifierStart(next)) {
                    key.append(next);
                    state = PART;
                } else {
                    throw new ParseException("Invalid Key Token", this.spanFrom(start));
                }
                break;

            case PART:
                if (Character.isJavaIdentifierPart(next) || next == '.') {
                    key.append(next);
                } else {
                    this.revert(1);
                    return this.lookupKeyword(key.toString(), start);
                }
                break;
            }
        }
        
        throw new ParseException("Unexpected EOS", this.spanFrom(start));
    }
    
    
    
    private Token lookupKeyword(String key, Position tokenStart) {
        TokenType result = keywords.get(key);
        if (result == null) {
            result = TokenType.IDENTIFIER;
        }
        return new Token(key, result, this.spanFrom(tokenStart));
    }
    
    
    
    private Token readString(Position start) throws ParseException {
        StringBuilder string = new StringBuilder();
        while (!this.eos) {
            char next = this.readChar();
            
            if (next == '"') {
                return new Token(string.toString(), TokenType.STRING, 
                        this.spanFrom(start));
            } else {
                string.append((char) next);
            }
        }
        
        throw new ParseException("Unclosed String-literal", this.spanFrom(start));
    }
    
    
    /** expecting sign */
    private final static int SIGN = 0;
    
    /** reading integer part of a number */
    private final static int INT_PART = 1;
    
    /** we hit a '.' after the integer part */
    private final static int DECIMAL_PART_TEMP = 2;
    
    /** we hit a '.' after the integer part */
    private final static int DECIMAL_PART= 3;
    
    private Token readNumber(Position start) throws ParseException {
        int numInt = 0;
        int sign = 1;
        double numDouble = 0.0;
        double dec = 1.0;
        
        int state = SIGN;
        
        while (!this.eos) {
            char next = this.readChar(true);
            
            switch (state) {
            case SIGN:
                if (next == '-') {
                    sign = -1;
                    state = INT_PART;
                }  else if (Character.isDigit(next)) {
                    this.revert(1);
                    state = INT_PART;
                }
                break;
                
            case INT_PART:
                if (Character.isDigit(next)) {
                    numInt = numInt * 10 + Character.getNumericValue(next);
                } else if (next == '.') {
                    numDouble = numInt;
                    state = DECIMAL_PART_TEMP;
                } else {
                    this.revert(1);
                    return new Token(numInt * sign, TokenType.INTEGER, 
                        this.spanFrom(start));
                }
                break;
                
            case DECIMAL_PART_TEMP:
                if (Character.isDigit(next)) {
                    state = DECIMAL_PART;
                } else {
                    this.revert(1);
                    throw new ParseException("Missing decimal part after '.'", 
                        this.spanFrom(start));
                }
                break;
                
            case DECIMAL_PART:
                if (Character.isDigit(next)) {
                    dec *= 0.1;
                    numDouble += Character.getNumericValue(next) * dec; 
                } else {
                    this.revert(1);
                    return new Token(numDouble * sign, TokenType.FLOAT, 
                        this.spanFrom(start));
                }
            }
        }
        
        throw new ParseException("Unexpected EOS", this.spanFrom(start));
    }
}