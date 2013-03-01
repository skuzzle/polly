package de.skuzzle.polly.core.parser.ast.visitor;

import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Braced;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;



/**
 * Provides an abstract {@link ASTVisitor} where all <code>before(XYZ)</code> and all 
 * <code>after(XYZ)</code> methods are pre-implemented empty.
 * 
 * @author Simon Taddiken
 */
public abstract class VisitorAdapter implements ASTVisitor {

    @Override
    public int before(Root root) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Root root) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(Literal literal) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Literal literal) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(Identifier identifier) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Identifier identifier) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(ResolvableIdentifier id) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(ResolvableIdentifier id) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(Assignment assign) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Assignment assign) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(Declaration decl) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Declaration decl) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(Call call) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Call call) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(OperatorCall call) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(OperatorCall call) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(Native hc) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Native hc) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(NamespaceAccess access) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(NamespaceAccess access) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(VarAccess access) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(VarAccess access) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(FunctionLiteral func) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(FunctionLiteral func) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(ListLiteral list) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(ListLiteral list) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(ProductLiteral product) throws ASTTraversalException {
        return CONTINUE;
    }
    
    @Override
    public int after(ProductLiteral product) throws ASTTraversalException {
        return CONTINUE;
    }
    
    @Override
    public int before(Braced braced) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Braced braced) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int before(Delete delete) throws ASTTraversalException {
        return CONTINUE;
    }

    @Override
    public int after(Delete delete) throws ASTTraversalException {
        return CONTINUE;
    }
    
    @Override
    public int before(Inspect inspect) throws ASTTraversalException {
        return CONTINUE;
    }
    
    @Override
    public int after(Inspect inspect) throws ASTTraversalException {
        return CONTINUE;
    }
    
    @Override
    public int before(Problem problem) throws ASTTraversalException {
        return CONTINUE;
    }
    
    @Override
    public int after(Problem problem) throws ASTTraversalException {
        return CONTINUE;
    }
}
