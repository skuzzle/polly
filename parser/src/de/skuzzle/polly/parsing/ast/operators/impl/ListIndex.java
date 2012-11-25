package de.skuzzle.polly.parsing.ast.operators.impl;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.operators.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;


public class ListIndex extends BinaryOperator<ListLiteral, NumberLiteral> {

    
    public ListIndex(OpType id) {
        super(id, Type.ANY, ListType.ANY_LIST, Type.NUMBER);
    }
    
    
    
    @Override
    protected void resolve(Expression left, Expression right, Namespace ns,
            Visitor typeResolver) throws ASTTraversalException {
        
        final ListType lt = (ListType) left.getType();
        this.setType(lt.getSubType());
    }
    

    
    @Override
    public Declaration createDeclaration() {
        final Declaration d = super.createDeclaration();
        d.setMustCopy(true);
        return d;
    }
    
    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral left,
            NumberLiteral right, Position resultPos) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case INDEX:
            stack.push((Literal) left.getContent().get(right.isInteger()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
