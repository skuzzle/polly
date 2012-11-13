package de.skuzzle.polly.parsing.ast.operators;


import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
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
        ADDWAVE("+~"),
        SUB("-"),
        MUL("*"),
        DIV("/"), 
        BOOLEAN_AND("&&"), 
        BOOLEAN_OR("||"), 
        DOLLAR("$"), 
        DOTDOT(".."), 
        EGT(">="), 
        ELT("<="), 
        EQ("=="), 
        EXCLAMATION("!"), 
        GT(">"), 
        INDEX("[]"), 
        INTDIV("\\"), 
        INT_AND("&"), 
        INT_OR("|"), 
        LEFT_SHIFT("<<"), 
        LT("<"), 
        MOD("%"), 
        NEQ("!="), 
        POWER("^"), 
        QUESTION("?"), 
        QUEST_EXCL("?!"), 
        RADIX("#"), 
        RIGHT_SHIFT(">>"), 
        URIGHT_SHIFT(">>>"), 
        WAVE("~");
        
        private final String id;
        
        
        private OpType(String id) {
            this.id = id;
        }
        
        
        
        public String getId() {
            return this.id;
        }
        
        
        
        /**
         * Converts from {@link TokenType} to Operator types.
         * @param token Token to convert into operator type.
         * @return The operator type.
         */
        public static OpType fromToken(Token token) {
            switch (token.getType()) {
            case ADD:         return OpType.ADD;
            case ADDWAVE:     return OpType.ADDWAVE;
            case BOOLEAN_AND: return OpType.BOOLEAN_AND;
            case BOOLEAN_OR:  return OpType.BOOLEAN_OR;
            case DIV:         return OpType.DIV;
            case DOLLAR:      return OpType.DOLLAR;
            case DOTDOT:      return OpType.DOTDOT;
            case EGT:         return OpType.EGT;
            case ELT:         return OpType.ELT;
            case EQ:          return OpType.EQ;
            case EXCLAMATION: return OpType.EXCLAMATION;
            case GT:          return OpType.GT;
            case INDEX:       return OpType.INDEX;
            case INTDIV:      return OpType.INTDIV;
            case INT_AND:     return OpType.INT_AND;
            case INT_OR:      return OpType.INT_OR;
            case LEFT_SHIFT:  return OpType.LEFT_SHIFT;
            case LT:          return OpType.LT;
            case MOD:         return OpType.MOD;
            case MUL:         return OpType.MUL;
            case NEQ:         return OpType.NEQ;
            case POWER:       return OpType.POWER;
            case QUESTION:    return OpType.QUESTION;
            case QUEST_EXCALAMTION: return OpType.QUEST_EXCL;
            case RADIX:       return OpType.RADIX;
            case RIGHT_SHIFT: return OpType.RIGHT_SHIFT;
            case SUB:         return OpType.SUB;
            case URIGHT_SHIFT:return OpType.URIGHT_SHIFT;
            case WAVE:        return OpType.WAVE;
            default:
                throw new IllegalArgumentException("not a valid operator token: " + 
                    token);
            
            }
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
