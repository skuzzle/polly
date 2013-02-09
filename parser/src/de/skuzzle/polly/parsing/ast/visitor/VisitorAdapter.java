package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.Inspect;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;



/**
 * Provides an abstract {@link Visitor} where all <code>beforeXYZ</code> and all 
 * <code>afterXYZ</code> methods are pre-implemented empty.
 * 
 * @author Simon Taddiken
 */
public abstract class VisitorAdapter implements Visitor {

    @Override
    public void beforeRoot(Root root) throws ASTTraversalException {}

    @Override
    public void afterRoot(Root root) throws ASTTraversalException {}

    @Override
    public void beforeLiteral(Literal literal) throws ASTTraversalException {}

    @Override
    public void afterLiteral(Literal literal) throws ASTTraversalException {}

    @Override
    public void beforeIdentifier(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void afterIdentifier(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void beforeResolvable(ResolvableIdentifier id) throws ASTTraversalException {}

    @Override
    public void afterResolvable(ResolvableIdentifier id) throws ASTTraversalException {}

    @Override
    public void beforeAssignment(Assignment assign) throws ASTTraversalException {}

    @Override
    public void afterAssignment(Assignment assign) throws ASTTraversalException {}

    @Override
    public void beforeDecl(Declaration decl) throws ASTTraversalException {}

    @Override
    public void afterDecl(Declaration decl) throws ASTTraversalException {}

    @Override
    public void beforeCall(Call call) throws ASTTraversalException {}

    @Override
    public void afterCall(Call call) throws ASTTraversalException {}

    @Override
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException {}

    @Override
    public void afterOperatorCall(OperatorCall call) throws ASTTraversalException {}

    @Override
    public void beforeNative(Native hc) throws ASTTraversalException {}

    @Override
    public void afterNative(Native hc) throws ASTTraversalException {}

    @Override
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException {}

    @Override
    public void afterAccess(NamespaceAccess access) throws ASTTraversalException {}

    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {}

    @Override
    public void afterVarAccess(VarAccess access) throws ASTTraversalException {}

    @Override
    public void beforeFunctionLiteral(FunctionLiteral func) 
        throws ASTTraversalException {}

    @Override
    public void afterFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {}

    @Override
    public void beforeListLiteral(ListLiteral list) throws ASTTraversalException {}

    @Override
    public void afterListLiteral(ListLiteral list) throws ASTTraversalException {}

    @Override
    public void beforeProductLiteral(ProductLiteral product) 
        throws ASTTraversalException {}
    
    @Override
    public void afterProductLiteral(ProductLiteral product) 
        throws ASTTraversalException {}
    
    @Override
    public void beforeBraced(Braced braced) throws ASTTraversalException {}

    @Override
    public void afterBraced(Braced braced) throws ASTTraversalException {}

    @Override
    public void beforeDelete(Delete delete) throws ASTTraversalException {}

    @Override
    public void afterDelete(Delete delete) throws ASTTraversalException {}
    
    @Override
    public void beforeInspect(Inspect inspect) throws ASTTraversalException {}
    
    @Override
    public void afterInspect(Inspect inspect) throws ASTTraversalException {}
}
