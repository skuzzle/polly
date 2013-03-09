package de.skuzzle.polly.core.parser.ast.visitor;

import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Braced;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;

/**
 * Visitor that sets the parent attribute for each node in the AST
 * 
 * @author Simon Taddiken
 */
public class ParentSetter extends DepthFirstVisitor {
    
    /** Convenience constant that holds an instance of this class */
    public final static ASTVisitor DEFAULT_INSTANCE = new ParentSetter();
    
    

    @Override
    public int before(NamespaceAccess node) throws ASTTraversalException {
        node.getLhs().setParent(node);
        node.getRhs().setParent(node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Assignment node) throws ASTTraversalException {
        node.getExpression().setParent(node);
        node.getName().setParent(node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Braced node) throws ASTTraversalException {
        node.getExpression().setParent(node);
        return CONTINUE;
    }
    
    
    @Override
    public int before(Call node) throws ASTTraversalException {
        node.getLhs().setParent(node);
        node.getRhs().setParent(node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(FunctionLiteral node) throws ASTTraversalException {
        node.getBody().setParent(node);
        for (final Declaration d : node.getFormal()) {
            d.setParent(node);
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(OperatorCall node) throws ASTTraversalException {
        this.before((Call) node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Root node) throws ASTTraversalException {
        node.getCommand().setParent(node);
        for(final Expression exp : node.getExpressions()) {
            exp.setParent(node);
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(VarAccess node) throws ASTTraversalException {
        node.getIdentifier().setParent(node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Declaration node) throws ASTTraversalException {
        node.getExpression().setParent(node);
        node.getName().setParent(node);
        return CONTINUE;
    }

    
    
    @Override
    public int before(Delete node) throws ASTTraversalException {
        for (final Identifier id : node.getIdentifiers()) {
            id.setParent(node);
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Inspect node) throws ASTTraversalException {
        node.getAccess().setParent(node);
        return CONTINUE;
    }
}
