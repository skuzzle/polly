package de.skuzzle.polly.parsing.ast.operators.binary;

import java.util.LinkedList;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.operators.BinaryOperator;
import de.skuzzle.polly.parsing.types.Type;

/**
 * Contains arithmetic operators that operator on {@link NumberLiteral}s.
 *  
 * @author Simon Taddiken
 */
public class Arithmetic extends BinaryOperator<NumberLiteral, NumberLiteral> {

    
    public Arithmetic(OpType id) {
        super(id, Type.NUMBER, Type.NUMBER, Type.NUMBER);
    }
    
    

    @Override
    protected void exec(LinkedList<Literal> stack, Namespace ns,
            NumberLiteral left, NumberLiteral right, Position resultPos) {
        
        switch (this.getId()) {
        case ADD: 
            stack.push(new NumberLiteral(resultPos, left.getValue() + right.getValue()));
            break;
        case SUB:
            stack.push(new NumberLiteral(resultPos, left.getValue() - right.getValue()));
            break;
        case MUL:
            stack.push(new NumberLiteral(resultPos, left.getValue() * right.getValue()));
            break;
        case DIV:
            stack.push(new NumberLiteral(resultPos, left.getValue() / right.getValue()));
            break;
        }
    }
}