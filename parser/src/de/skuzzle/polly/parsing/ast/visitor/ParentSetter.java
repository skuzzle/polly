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
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException {
        access.getLhs().setParent(access);
        access.getRhs().setParent(access);
    }
    
    
    
    @Override
    public void beforeAssignment(Assignment assign) throws ASTTraversalException {
        assign.getExpression().setParent(assign);
        assign.getName().setParent(assign);
    }
    
    
    
    @Override
    public void beforeBraced(Braced braced) throws ASTTraversalException {
        braced.getExpression().setParent(braced);
    }
    
    
    @Override
    public void beforeCall(Call call) throws ASTTraversalException {
        call.getLhs().setParent(call);
        call.getRhs().setParent(call);
    }
    
    
    
    @Override
    public void beforeFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        func.getBody().setParent(func);
        for (final Declaration d : func.getFormal()) {
            d.setParent(func);
        }
    }
    
    
    
    @Override
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeCall(call);
    }
    
    
    
    @Override
    public void beforeRoot(Root root) throws ASTTraversalException {
        root.getCommand().setParent(root);
        for(final Expression exp : root.getExpressions()) {
            exp.setParent(root);
        }
    }
    
    
    
    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {
        access.getIdentifier().setParent(access);
    }
    
    
    
    @Override
    public void beforeDecl(Declaration decl) throws ASTTraversalException {
        decl.getExpression().setParent(decl);
        decl.getName().setParent(decl);
    }

    
    
    @Override
    public void beforeDelete(Delete delete) throws ASTTraversalException {
        for (final Identifier id : delete.getIdentifiers()) {
            id.setParent(delete);
        }
    }
    
    
    
    @Override
    public void beforeInspect(Inspect inspect) throws ASTTraversalException {
        inspect.getAccess().setParent(inspect);
    }
}
