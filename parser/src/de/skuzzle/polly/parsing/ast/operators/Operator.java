package de.skuzzle.polly.parsing.ast.operators;


import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.HardcodedExpression;
import de.skuzzle.polly.parsing.types.Type;

/**
 * Superclass for all operators. Operators are represented as a normal function call and
 * must thus exist as an expression that can be executed when that call is resolved.
 * 
 * <p>Subclasses should only be used in declarations, but not as Node in the AST.</p> 
 * 
 * @author Simon Taddiken
 */
public abstract class Operator extends HardcodedExpression {
    
    /**
     * All possible operator types and their string representation.
     * 
     * @author Simon Taddiken
     */
    public static enum OpType {
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIV("/");
        
        private final String id;
        
        
        private OpType(String id) {
            this.id = id;
        }
        
        
        
        public String getId() {
            return this.id;
        }
    }

    
    
    private final OpType id;
    
    
    
    /**
     * Creates a new operator.
     * 
     * @param id The operator type
     * @param type The type of the value that this operator returns.
     */
    public Operator(OpType id, Type type) {
        super(type);
        this.id = id;
    }
    
    

    /**
     * Gets the identifier that represents this operator.
     * 
     * @return The identifier.
     */
    public OpType getId() {
        return this.id;
    }

    
    
    /**
     * Creates a proper function declaration for this parameter.
     * 
     * @return A functionDeclaration.
     */
    public abstract FunctionDeclaration createDeclaration();
}
