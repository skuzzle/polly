package de.skuzzle.polly.parsing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.ast.declarations.FunctionParameter;
import de.skuzzle.polly.parsing.ast.declarations.ListParameter;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ChannelLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.UserLiteral;
import de.skuzzle.polly.parsing.types.Type;


public class PollyV91InputParser extends AbstractParser<InputScanner> {

    private PrecedenceTable operators;
    private int openExpressions;
    
    
    
    @Override
    public Root parse(String input, String encoding) throws ParseException,
        UnsupportedEncodingException {
        return null;
    }

    @Override
    public Root tryParse(String input, String encoding) throws ParseException,
        UnsupportedEncodingException {
        return null;
    }

    @Override
    protected Root parse_input() throws ParseException {
        return null;
    }
    
    
    
    protected Identifier expectIdentifier() throws ParseException {
        final Token la = this.scanner.lookAhead();
        if (!la.matches(TokenType.IDENTIFIER)) {
            this.expect(TokenType.IDENTIFIER);
        }
        return new Identifier(la.getPosition(), la.getStringValue());
    }

    
    
    protected void allowSingleWhiteSpace() throws ParseException {
        this.scanner.match(TokenType.SEPERATOR);
    }
    
    
    
    protected void enterExpression() {
        ++this.openExpressions;
        this.scanner.setSkipWhiteSpaces(true);
    }
    
    
    
    protected void leaveExpression() {
        --this.openExpressions;
        if (this.openExpressions == 0) {
            this.scanner.setSkipWhiteSpaces(false);
        }
    }
    
    
    
    /**
     * Parses a list of formal parameters that ends with the token type <code>end</code>.
     * 
     * <pre>
     * parameters -> end   // empty list
     *             | parameter (',' parameter)*
     * </pre>
     * @param end The token that the list is supposed to end with (to determine empty 
     *          lists, token won't be consumed if hit).
     * @return Collection of parsed formal parameters.
     * @throws ParseException If parsing fails.
     */
    protected Collection<Parameter> parseParameters(TokenType end) throws ParseException {
        if (this.scanner.lookAhead().matches(end)) {
            // empty list.
            return new ArrayList<Parameter>(0);
        }
        
        final Collection<Parameter> result = new ArrayList<Parameter>();
        result.add(this.parseParameter());
        
        while (this.scanner.match(TokenType.COMMA)) {
            this.allowSingleWhiteSpace();
            result.add(this.parseParameter());
        }
        return result;
    }
    
    
    
    /**
     * <pre>
     * parameter -> id id             // simple parameter
     *            | id<id> id         // parameter with subtype (list)
     *            | '\(' id (',' id+ ')' id   // function parameter
     * </pre>
     * @return
     * @throws ParseException
     */
    protected Parameter parseParameter() throws ParseException {
        final Token la = this.scanner.lookAhead();
        
        if (this.scanner.match(TokenType.LAMBDA)) {
            final ResolvableIdentifier returnType = new ResolvableIdentifier(
                this.expectIdentifier());
            
            final Collection<ResolvableIdentifier> sig = 
                new ArrayList<ResolvableIdentifier>();
            sig.add(returnType);
            
            while (this.scanner.match(TokenType.COMMA)) {
                sig.add(new ResolvableIdentifier(this.expectIdentifier()));
            }
            this.expect(TokenType.CLOSEDBR);
            
            final ResolvableIdentifier name = new ResolvableIdentifier(
                this.expectIdentifier());
            return new FunctionParameter(this.scanner.spanFrom(la), sig, name);
        } else {
             final ResolvableIdentifier typeName = new ResolvableIdentifier(
                 this.expectIdentifier());
             if (this.scanner.match(TokenType.LT)) {
                 final ResolvableIdentifier subType = new ResolvableIdentifier(
                     this.expectIdentifier());
                 this.scanner.match(TokenType.GT);
                 
                 final ResolvableIdentifier name = new ResolvableIdentifier(
                     this.expectIdentifier());
                 return new ListParameter(this.scanner.spanFrom(la), subType, name);
             } else {
                 final ResolvableIdentifier name = new ResolvableIdentifier(
                     this.expectIdentifier());
                 return new Parameter(this.scanner.spanFrom(la), typeName, name);
             }
        }
    }
    
    
    
    protected Expression parseExpression() throws ParseException {
        
        return null;
    }
    
    
    
    protected Expression parseLiteral() throws ParseException {
        final Token la = this.scanner.lookAhead();
        int start = 0;
        
        switch(la.getType()) {
        case IDENTIFIER:
            this.scanner.consume();
            
            final ResolvableIdentifier id = new ResolvableIdentifier(
                la.getPosition(), la.getStringValue());
            
            if (this.scanner.match(TokenType.OPENBR)) {
                final Call call = new Call(id.getPosition(), id, 
                    this.parseExpressionList(TokenType.CLOSEDBR));
                
                this.expect(TokenType.CLOSEDBR);
                return call;
            } else {
                return new VarAccess(la.getPosition(), id);
            }
            
        case LAMBDA:
            start = this.scanner.getStreamIndex();
            this.scanner.consume();
            
            final Collection<Parameter> formal = this.parseParameters(
                TokenType.SEMICOLON);
            this.expect(TokenType.SEMICOLON);
            
            final Expression exp = this.parseExpression();
            
            this.expect(TokenType.CLOSEDBR);
            
            return new FunctionLiteral(this.scanner.spanFrom(start), formal, exp);
            
        case OPENCURLBR:
            start = this.scanner.getStreamIndex();
            this.scanner.consume();
            
            final Collection<Expression> elements = this.parseExpressionList(
                TokenType.CLOSEDCURLBR);
            
            this.expect(TokenType.CLOSEDCURLBR);
            
            final ListLiteral list = new ListLiteral(this.scanner.spanFrom(start), 
                elements);
            
        case TRUE:
            this.scanner.consume();
            return new BooleanLiteral(la.getPosition(), true);
        case FALSE:
            this.scanner.consume();
            return new BooleanLiteral(la.getPosition(), false);
                
        case CHANNEL:
            this.scanner.consume();
            return new ChannelLiteral(la.getPosition(), la.getStringValue());

        case USER:
            this.scanner.consume();
            return new UserLiteral(la.getPosition(), la.getStringValue());
            
        case STRING:
            this.scanner.consume();
            return new StringLiteral(la.getPosition(), la.getStringValue());

        case NUMBER:
            this.scanner.consume();
            return new NumberLiteral(la.getPosition(), la.getFloatValue());
        }
    }
    
    
    
    /**
     * Parses a comma separated list of expressions. The <code>end</code> token type
     * exists only for determining empty lists.
     * 
     * <pre>
     * exprList -> end   // empty list
     *           | expression (',' expression)*
     * </pre>
     * @param end The token which should end the list. Only used to determine empty lists.
     * @return A collection of parsed expressions.
     * @throws ParseException If parsing fails.
     */
    protected Collection<Expression> parseExpressionList(TokenType end) 
            throws ParseException {
        
        // do not consume here. end token is consume by the caller
        if (this.scanner.lookAhead().matches(end)) {
            // empty list
            return new ArrayList<Expression>(0);
        }
        
        final Collection<Expression> result = new ArrayList<Expression>();
        
        result.add(this.parseExpression());
        
        while (!scanner.match(TokenType.COMMA)) {
            this.allowSingleWhiteSpace();
            result.add(this.parseExpression());
        }
        return result;
    }
}