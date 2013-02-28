package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Inspect;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;

/**
 * Visitor that sets the parent attribute for each node in the AST
 * 
 * @author Simon Taddiken
 */
public class ParentSetter extends DepthFirstVisitor {
    
    /** Convenience constant that holds an instance of this class */
    public final static ASTVisitor DEFAULT_INSTANCE = new ParentSetter();
    
    

    @Override
    public int before(NamespaceAccess access) throws ASTTraversalException {
        access.getLhs().setParent(access);
        access.getRhs().setParent(access);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Assignment assign) throws ASTTraversalException {
        assign.getExpression().setParent(assign);
        assign.getName().setParent(assign);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Braced braced) throws ASTTraversalException {
        braced.getExpression().setParent(braced);
        return CONTINUE;
    }
    
    
    @Override
    public int before(Call call) throws ASTTraversalException {
        call.getLhs().setParent(call);
        call.getRhs().setParent(call);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(FunctionLiteral func) throws ASTTraversalException {
        func.getBody().setParent(func);
        for (final Declaration d : func.getFormal()) {
            d.setParent(func);
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(OperatorCall call) throws ASTTraversalException {
        this.before((Call)call);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Root root) throws ASTTraversalException {
        root.getCommand().setParent(root);
        for(final Expression exp : root.getExpressions()) {
            exp.setParent(root);
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(VarAccess access) throws ASTTraversalException {
        access.getIdentifier().setParent(access);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Declaration decl) throws ASTTraversalException {
        decl.getExpression().setParent(decl);
        decl.getName().setParent(decl);
        return CONTINUE;
    }

    
    
    @Override
    public int before(Delete delete) throws ASTTraversalException {
        for (final Identifier id : delete.getIdentifiers()) {
            id.setParent(delete);
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Inspect inspect) throws ASTTraversalException {
        inspect.getAccess().setParent(inspect);
        return CONTINUE;
    }
}
