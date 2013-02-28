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
 * Provides an abstract {@link ASTVisitor} where all <code>before(XYZ)</code> and all 
 * <code>after(XYZ)</code> methods are pre-implemented empty.
 * 
 * @author Simon Taddiken
 */
public abstract class VisitorAdapter implements ASTVisitor {

    @Override
    public void before(Root root) throws ASTTraversalException {}

    @Override
    public void after(Root root) throws ASTTraversalException {}

    @Override
    public void before(Literal literal) throws ASTTraversalException {}

    @Override
    public void after(Literal literal) throws ASTTraversalException {}

    @Override
    public void before(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void after(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void before(ResolvableIdentifier id) throws ASTTraversalException {}

    @Override
    public void after(ResolvableIdentifier id) throws ASTTraversalException {}

    @Override
    public void before(Assignment assign) throws ASTTraversalException {}

    @Override
    public void after(Assignment assign) throws ASTTraversalException {}

    @Override
    public void before(Declaration decl) throws ASTTraversalException {}

    @Override
    public void after(Declaration decl) throws ASTTraversalException {}

    @Override
    public void before(Call call) throws ASTTraversalException {}

    @Override
    public void after(Call call) throws ASTTraversalException {}

    @Override
    public void before(OperatorCall call) throws ASTTraversalException {}

    @Override
    public void after(OperatorCall call) throws ASTTraversalException {}

    @Override
    public void before(Native hc) throws ASTTraversalException {}

    @Override
    public void after(Native hc) throws ASTTraversalException {}

    @Override
    public void before(NamespaceAccess access) throws ASTTraversalException {}

    @Override
    public void after(NamespaceAccess access) throws ASTTraversalException {}

    @Override
    public void before(VarAccess access) throws ASTTraversalException {}

    @Override
    public void after(VarAccess access) throws ASTTraversalException {}

    @Override
    public void before(FunctionLiteral func) throws ASTTraversalException {}

    @Override
    public void after(FunctionLiteral func) throws ASTTraversalException {}

    @Override
    public void before(ListLiteral list) throws ASTTraversalException {}

    @Override
    public void after(ListLiteral list) throws ASTTraversalException {}

    @Override
    public void before(ProductLiteral product) throws ASTTraversalException {}
    
    @Override
    public void after(ProductLiteral product) throws ASTTraversalException {}
    
    @Override
    public void before(Braced braced) throws ASTTraversalException {}

    @Override
    public void after(Braced braced) throws ASTTraversalException {}

    @Override
    public void before(Delete delete) throws ASTTraversalException {}

    @Override
    public void after(Delete delete) throws ASTTraversalException {}
    
    @Override
    public void before(Inspect inspect) throws ASTTraversalException {}
    
    @Override
    public void after(Inspect inspect) throws ASTTraversalException {}
}
