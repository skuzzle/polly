package de.skuzzle.polly.parsing.ast.operators;


import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Hardcoded;
import de.skuzzle.polly.parsing.types.Type;

/**
 * Superclass for all operators. Operators are represented as a normal function call and
 * must thus exist as an expression that can be executed when that call is resolved.
 * 
 * <p>Subclasses should only be used in declarations, but not as Node in the AST.</p> 
 * 
 * @author Simon Taddiken
 */
public abstract class Operator extends Hardcoded {
    
    /**
     * All possible operator types and their string representation.
     * 
     * @author Simon Taddiken
     */
    public static enum OpType {
        // casting
        STRING(Type.STRING.getTypeName().getId()),
        NUMBER(Type.NUMBER.getTypeName().getId()),
        DATE(Type.DATE.getTypeName().getId()),
        TIMESPAN(Type.TIMESPAN.getTypeName().getId()),
        
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

    
    
    private final OpType op;
    
    
    
    /**
     * Creates a new operator.
     * 
     * @param op The operator type
     * @param type The type of the value that this operator returns.
     */
    public Operator(OpType op, Type type) {
        super(type);
        this.op = op;
    }
    
    

    /**
     * Gets the identifier that represents this operator.
     * 
     * @return The identifier.
     */
    public OpType getOp() {
        return this.op;
    }

    
    
    /**
     * Creates a proper declaration for this operator.
     * 
     * @return A declaration.
     */
    public abstract Declaration createDeclaration();
}
