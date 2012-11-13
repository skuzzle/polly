package de.skuzzle.polly.parsing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.PrecedenceTable.PrecedenceLevel;
import de.skuzzle.polly.parsing.ast.declarations.FunctionParameter;
import de.skuzzle.polly.parsing.ast.declarations.ListParameter;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ChannelLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.DateLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.UserLiteral;
import de.skuzzle.polly.parsing.ast.operators.Operator.OpType;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
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
    
    
    

    
    
    
    protected Expression parseExpression() throws ParseException {
        
        return null;
    }
    
    
    
    /**
     * Parses the '..' range operator, which can either be a binary operator or
     * a ternary operator if an additional step size is recognized.
     * 
     * <pre>
     * dotdot -> unary ('..' unary ('$' unary)?)?   // range operator with optional 
     *                                              // step size
     * </pre>
     * @return The parsed operator or the expression from the next higher precedence level
     *          if no DOTDOT operator was found. 
     * @throws ParseException If parsing fails.
     */
    protected Expression parseDotDot() throws ParseException {
        final Expression lhs = this.parseUnary();
        
        final Token la = this.scanner.lookAhead();
        if (this.operators.match(la, PrecedenceLevel.DOTDOT)) {
            this.scanner.consume();
            
            final Expression endRange = this.parseUnary();
            if (this.scanner.match(TokenType.DOLLAR)) {
                final Expression operand3 = this.parseUnary();
                return OperatorCall.ternary(
                    new Position(lhs.getPosition(), operand3.getPosition()), 
                    OpType.fromToken(la), lhs, endRange, operand3);
            } else {
                return OperatorCall.binary(
                    new Position(lhs.getPosition(), endRange.getPosition()), 
                    OpType.fromToken(la), lhs, endRange);
            }
        }
        return lhs;
    }
    
    
    
    /**
     * Parses an unary operator call.
     * <pre>
     * unary -> UNARY_OP unary    // right-associative unary operator
              | access
     * </pre>
     * @return A unary operator call or the expression returned by the next higher
     *          precedence level if no UNARY_OP was found.
     * @throws ParseException If parsing fails.
     */
    protected Expression parseUnary() throws ParseException {
        final Token la = this.scanner.lookAhead();
        
        if (this.operators.match(la, PrecedenceLevel.UNARY)) {
            this.scanner.consume();
            final Expression rhs = this.parseUnary();
            return OperatorCall.unary(new Position(la.getPosition(), 
                    rhs.getPosition()), OpType.fromToken(la), rhs);
        } else {
            return this.parseNamespaceAccess();
        }
    }
    
    
    
    /**
     * Parses a {@link Namespace} access.
     * <pre>
     * literal ('.' varOrCall)?
     * </pre>
     * 
     * @return The parsed literal if no DOT operator was found, or a {@link Namespace}
     *          access if the was a dot followed by a VarOrCall.
     * @throws ParseException If parsing fails
     */
    protected Expression parseNamespaceAccess() throws ParseException {
        final Expression lhs = this.parseLiteral();
        
        final Token la = this.scanner.lookAhead();
        if (this.scanner.match(TokenType.DOT)) {
            final VarAccess rhs = this.parseVarOrCall();
            
            if (rhs == null) {
                throw new ParseException("Erwartet: Variable oder Funktionsaufruf", 
                    this.scanner.spanFrom(lhs.getPosition().getStart()));
            }
            return new NamespaceAccess(new Position(lhs.getPosition(), 
                this.scanner.spanFrom(la)), lhs, rhs);
        }
        
        return lhs;
    }
    
    
    
    protected Expression parseLiteral() throws ParseException {
        final VarAccess va = this.parseVarOrCall();
        // check if var or call. if so, return, if not, test for other literals
        if (va != null) {
            return va;
        }
        
        
        final Token la = this.scanner.lookAhead();
        Expression exp = null;
        
        switch(la.getType()) {
        case OPENBR:
            this.scanner.consume();
            
            /*
             * Now we can ignore whitespaces until the matching closing brace is 
             * read.
             */
            this.enterExpression();
            
            exp = this.parseExpression();
            this.expect(TokenType.CLOSEDBR);
            exp.setPosition(this.scanner.spanFrom(la));
            
            this.leaveExpression();
            return exp;
            
        case LAMBDA:
            this.scanner.consume();
            
            final Collection<Parameter> formal = this.parseParameters(
                TokenType.SEMICOLON);
            this.expect(TokenType.SEMICOLON);
            
            exp = this.parseExpression();
            
            this.expect(TokenType.CLOSEDBR);
            exp.setPosition(this.scanner.spanFrom(la));
            
            return new FunctionLiteral(this.scanner.spanFrom(la), formal, exp);
            
        case OPENCURLBR:
            this.scanner.consume();
            
            final Collection<Expression> elements = this.parseExpressionList(
                TokenType.CLOSEDCURLBR);
            
            this.expect(TokenType.CLOSEDCURLBR);
            
            final ListLiteral list = new ListLiteral(this.scanner.spanFrom(la), 
                elements);
            
            this.expect(TokenType.CLOSEDCURLBR);
            list.setPosition(this.scanner.spanFrom(la));
            return list;
            
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
            
        case DATETIME:
            this.scanner.consume();
            return new DateLiteral(la.getPosition(), la.getDateValue());
            
        case TIMESPAN:
            this.scanner.consume();
            return new TimespanLiteral(la.getPosition(), (int)la.getLongValue());
        default:
            this.expect(TokenType.LITERAL);
        }
        
        return null;
    }
    
    
    
    /**
     * Tries to parse a variable access or function call. If the next tokens form 
     * no valid function call (i.e. the next token is no identifier, this method 
     * returns <code>null</code>).
     * 
     * <pre>
     * varOrCall -> ID                    // var access
     *            | ID '(' exprList ')'   // call   
     *            | epsilon
     * </pre>
     * @return Either a {@link VarAccess} instance if the next token is a single 
     *          identifier, or a {@link Call} if the next tokens form a valid 
     *          function call, or <code>null</code> if the next token is no 
     *          identifier.
     * @throws ParseException If this was no valid call though it appeared to be one. 
     */
    protected VarAccess parseVarOrCall() throws ParseException {
        final Token la = this.scanner.lookAhead();
        
        if (la.getType() == TokenType.IDENTIFIER) {
            this.scanner.consume();
            
            final ResolvableIdentifier id = new ResolvableIdentifier(
                la.getPosition(), la.getStringValue());
            
            if (this.scanner.match(TokenType.OPENBR)) {
                final Call call = new Call(id.getPosition(), id, 
                    this.parseExpressionList(TokenType.CLOSEDBR));
                
                this.expect(TokenType.CLOSEDBR);
                call.setPosition(this.scanner.spanFrom(la));
                return call;
            } else {
                return new VarAccess(la.getPosition(), id);
            }
        }
        
        // indicates that this was no var access or function call.
        return null;
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
     *            | '\(' id (',' id)* ')' id   // function parameter
     * </pre>
     * @return The parsed parameter.
     * @throws ParseException If parsing fails.
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
                this.allowSingleWhiteSpace();
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
                 return new ListParameter(
                     this.scanner.spanFrom(la), typeName, subType, name);
                 
             } else {
                 final ResolvableIdentifier name = new ResolvableIdentifier(
                     this.expectIdentifier());
                 return new Parameter(this.scanner.spanFrom(la), typeName, name);
             }
        }
    }
}