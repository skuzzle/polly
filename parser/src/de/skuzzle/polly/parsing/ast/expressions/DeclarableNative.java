package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.parameters.ResolvableType;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;


public class DeclarableNative extends Native {

    private static final long serialVersionUID = 1L; 

    private ResolvableType resolvableType;
    
    
    public DeclarableNative(ResolvableType resolvableType) {
        super(Type.UNKNOWN);
        this.resolvableType = resolvableType;
    }
    
    
    
    @Override
    public void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException {
        this.setUnique(this.resolvableType.resolve());
    }



    @Override
    public void execute(Stack<Literal> stack, Namespace ns, Visitor execVisitor)
        throws ASTTraversalException {}
}
