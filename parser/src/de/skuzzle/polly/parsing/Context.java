package de.skuzzle.polly.parsing;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.ResolveableIdentifierLiteral;
import de.skuzzle.polly.parsing.tree.operators.BinaryOperatorOverload;
import de.skuzzle.polly.parsing.tree.operators.BinaryOperators;
import de.skuzzle.polly.parsing.tree.operators.TernaryDotDotOperator;
import de.skuzzle.polly.parsing.tree.operators.TernaryOperatorOverload;
import de.skuzzle.polly.parsing.tree.operators.UnaryOperatorOverload;
import de.skuzzle.polly.parsing.tree.operators.UnaryOperators;




public class Context {
    
    
    private final static List<BinaryOperatorOverload> BINARY_OPERATORS;
    private final static List<UnaryOperatorOverload> UNARY_OPERATORS;
    private final static List<TernaryOperatorOverload> TERNARY_OPERATORS;
    
    static {
        BINARY_OPERATORS = new LinkedList<BinaryOperatorOverload>();
        
        BINARY_OPERATORS.add(new BinaryOperators.IndexListOperator());
        BINARY_OPERATORS.add(new BinaryOperators.ListArithmeticOperator(TokenType.ADD));
        BINARY_OPERATORS.add(new BinaryOperators.ListArithmeticOperator(TokenType.SUB));
        BINARY_OPERATORS.add(new BinaryOperators.ListArithmeticOperator(TokenType.WAVE));
        BINARY_OPERATORS.add(new BinaryOperators.ListArithmeticOperator(TokenType.ADDWAVE));
        
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.ADD));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.SUB));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.MUL));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.DIV));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.INTDIV));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.INT_AND));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.INT_XOR));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.INT_OR));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.POWER));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.MOD));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.LEFT_SHIFT));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.RIGHT_SHIFT));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.URIGHT_SHIFT));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticOperator(TokenType.CHOOSE));
        
        //BINARY_OPERATORS.add(new BinaryOperators.ArithmeticDateOperator(TokenType.ADD));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticDateOperator(TokenType.SUB));
        BINARY_OPERATORS.add(new BinaryOperators.ArithemticDateTimespanOperator(TokenType.SUB));
        BINARY_OPERATORS.add(new BinaryOperators.ArithemticDateTimespanOperator(TokenType.ADD));
        
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticTimespanOperator(TokenType.ADD));
        BINARY_OPERATORS.add(new BinaryOperators.ArithmeticTimespanOperator(TokenType.SUB));
                
        BINARY_OPERATORS.add(new BinaryOperators.ConcatStringOperator());
        BINARY_OPERATORS.add(new BinaryOperators.IndexStringOperator());

        BINARY_OPERATORS.add(new BinaryOperators.BooleanOperator(TokenType.BOOLEAN_AND));
        BINARY_OPERATORS.add(new BinaryOperators.BooleanOperator(TokenType.BOOLEAN_OR));
        BINARY_OPERATORS.add(new BinaryOperators.BooleanOperator(TokenType.XOR));
        
        BINARY_OPERATORS.add(new BinaryOperators.EqualityOperator(TokenType.EQ));
        BINARY_OPERATORS.add(new BinaryOperators.EqualityOperator(TokenType.NEQ));
        
        BINARY_OPERATORS.add(new BinaryOperators.RelationalDateOperator(TokenType.LT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalDateOperator(TokenType.ELT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalDateOperator(TokenType.GT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalDateOperator(TokenType.EGT));
        
        BINARY_OPERATORS.add(new BinaryOperators.RelationalNumberOperator(TokenType.LT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalNumberOperator(TokenType.ELT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalNumberOperator(TokenType.GT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalNumberOperator(TokenType.EGT));
        
        BINARY_OPERATORS.add(new BinaryOperators.RelationalStringOperator(TokenType.LT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalStringOperator(TokenType.ELT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalStringOperator(TokenType.GT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalStringOperator(TokenType.EGT));
        
        BINARY_OPERATORS.add(new BinaryOperators.RelationalListOperator(TokenType.LT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalListOperator(TokenType.ELT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalListOperator(TokenType.GT));
        BINARY_OPERATORS.add(new BinaryOperators.RelationalListOperator(TokenType.EGT));
        
        
        UNARY_OPERATORS = new LinkedList<UnaryOperatorOverload>();
        UNARY_OPERATORS.add(new UnaryOperators.ArithmeticOperator(TokenType.SUB));
        UNARY_OPERATORS.add(new UnaryOperators.BooleanOperator(TokenType.EXCLAMATION));
        UNARY_OPERATORS.add(new UnaryOperators.StringOperator(TokenType.EXCLAMATION));
        UNARY_OPERATORS.add(new UnaryOperators.ListOperator(TokenType.EXCLAMATION));
        UNARY_OPERATORS.add(new UnaryOperators.RandomListIndexOperator());
        UNARY_OPERATORS.add(new UnaryOperators.TimespanOperator());
        
        
        TERNARY_OPERATORS = new LinkedList<TernaryOperatorOverload>();
        TERNARY_OPERATORS.add(new TernaryDotDotOperator());
    }
    

    
    public static void printOperators(PrintStream ps) {
        ps.println("BINARY OPERATORS:");
        for (BinaryOperatorOverload boo: BINARY_OPERATORS) {
            ps.println(boo.toString());
        }
        
        ps.println();
        ps.println("UNARY OPERATORS:");
        for (UnaryOperatorOverload uo : UNARY_OPERATORS) {
            ps.println(uo.toString());
        }
    }
    
    
    
    private Declarations myNameSpace;
    private Declarations currentNamespace;
    private Namespaces namespaces;
    private List<BinaryOperatorOverload> binaryOperators;
    private List<UnaryOperatorOverload> unaryOperators;
    
    
    

    public Context(Declarations defaultNamespace, Namespaces namespaces) {
        this.myNameSpace = defaultNamespace;
        this.currentNamespace = defaultNamespace;
        this.namespaces = namespaces;
        this.binaryOperators = BINARY_OPERATORS;
        this.unaryOperators = UNARY_OPERATORS;
    }
    
    
    
    public Declarations getCurrentNamespace() {
        return this.currentNamespace;
    }
    
    
    public Namespaces getNamespaces() {
        return this.namespaces;
    }
    
    
    
    public void switchNamespace(ResolveableIdentifierLiteral name) throws ParseException {
        this.currentNamespace = this.namespaces.get(name);
    }
    
    
    
    public void switchDefaultNamespace() {
        this.currentNamespace = this.myNameSpace;
    }

    
    
    public Expression resolveVar(ResolveableIdentifierLiteral name) 
            throws ParseException {
        if (this.currentNamespace == this.myNameSpace) {
            return this.myNameSpace.resolve(name);
        }
        
        try {
            return this.currentNamespace.resolve(name);
        } catch (ParseException goOn) {}
        
        return this.myNameSpace.resolve(name);
    }
    
    
    
    private void unaryOverloadException(TokenType operator, Type type, 
            Position position) throws ParseException {
        throw new ParseException("Operator '" + operator + "' nicht anwendbar auf den " +
                "Typ " + type, position);
    }    
    
    
    
    private void binaryOverloadException(TokenType operator, 
            Type left, Type right, Position position) throws ParseException {
        throw new ParseException("Operator '" + operator + "' nicht anwendbar auf die " +
                "Typen " + left + " und " + right, position);
                
    }
    
    
    
    private void ternaryOverloadException(TokenType operator, Type first, 
            Type second, Type third, Position position) throws ParseException {
        throw new ParseException("Operator '" + operator + "' nicht anwendbar auf die " +
                "Typen " + first + " und " + second + " und " + third, position);
                
    }
    
    
    
    public TernaryOperatorOverload resolveOverload(TokenType operator, Type first, 
            Type second, Type third, Position position) throws ParseException {

        for (TernaryOperatorOverload o : TERNARY_OPERATORS) {
            TernaryOperatorOverload r = o.match(operator, first, second, third);
            if (r != null) {
                return (TernaryOperatorOverload) r.clone();
            }
        }
        
        this.ternaryOverloadException(operator, first, second, third, position);
        return null;
    }
    
    
    
    public BinaryOperatorOverload resolveOverload(TokenType operator, Type left, 
            Type right, Position position) throws ParseException {

        for (BinaryOperatorOverload o : this.binaryOperators) {
            BinaryOperatorOverload r = o.match(operator, left, right);
            if (r != null) {
                return (BinaryOperatorOverload) r.clone();
            }
        }
        
        this.binaryOverloadException(operator, left, right, position);
        return null;
    }
    
    
    
    public UnaryOperatorOverload resolveOverload(
            TokenType operator, Type type, Position position) throws ParseException {

        for (UnaryOperatorOverload o : this.unaryOperators) {
            UnaryOperatorOverload r = o.match(operator, type);
            if (r != null) {
                return (UnaryOperatorOverload) r.clone();
            }
        }
        
        this.unaryOverloadException(operator, type, position);
        return null;
    }
    
}
