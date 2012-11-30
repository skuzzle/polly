package de.skuzzle.polly.parsing.ast.operators.impl;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.operators.TernaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;



/**
 * This implements a ternary conditional operator that behaves like the ?: in
 * Java.
 * 
 * @author Simon Taddiken
 */
public class Conditional extends TernaryOperator<BooleanLiteral, Literal, Literal>{

    private static final long serialVersionUID = 1L;



    public Conditional(OpType id) {
        super(id, Type.UNKNOWN, Type.BOOLEAN, Type.ANY, Type.ANY);
    }

    
    
    @Override
    public Declaration createDeclaration() {
        final Declaration d = super.createDeclaration();
        d.setMustCopy(true);
        return d;
    }
    
    
    
    @Override
    protected void resolve(Expression first, Expression second, Expression third,
            Namespace ns, Visitor typeResolver) throws ASTTraversalException {
        
        if (second.getType().check(third.getType())) {
            throw new ASTTraversalException(third.getPosition(), 
                "Operanden müssen den selben Typ haben.");
        }
        this.setType(second.getType());
    }
    
    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, BooleanLiteral first,
            Literal second, Literal third, Position resultPos) 
                throws ASTTraversalException {
        
        if (first.getValue()) {
            stack.push(second);
        } else {
            stack.push(third);
        }
    }
    

}
