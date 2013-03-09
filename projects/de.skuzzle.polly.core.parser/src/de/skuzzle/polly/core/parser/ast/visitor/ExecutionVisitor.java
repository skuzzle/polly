package de.skuzzle.polly.core.parser.ast.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Delete.DeleteableIdentifier;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.tools.collections.LinkedStack;
import de.skuzzle.polly.tools.collections.Stack;



public class ExecutionVisitor extends DepthFirstVisitor {

    protected final Stack<Literal> stack;
    private Namespace nspace;
    private final Namespace rootNs;
    
    public ExecutionVisitor(Namespace rootNs, Namespace workingNs) {
        this.stack = new LinkedStack<Literal>();
        this.nspace = workingNs;
        this.rootNs = rootNs;
    }
    
    
    
    /**
     * Determines whether we have a unique result (e.g. stack has only one element).
     * 
     * @return Whether we have a unique result.
     */
    public boolean hasResult() {
        return this.stack.size() == 1;
    }
    
    
    
    /**
     * Creates a new sub namespace of the current namespace and sets that new namespace
     * as the current one.
     * 
     * @return The created namespace.
     */
    private Namespace enter() {
        return this.nspace = this.nspace.enter();
    }
    
    
    
    /**
     * Sets the current namespace as the parent of the current namespace.
     * 
     * @return The parent of the former current namespace.
     */
    private Namespace leave() {
        return this.nspace = this.nspace.getParent();
    }
    
    
    
    @Override
    public boolean visit(Root node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        final List<Literal> results = 
            new ArrayList<Literal>(node.getExpressions().size());
        
        for (final Expression exp : node.getExpressions()) {
            if (!exp.visit(this)) {
                return false;
            }
            results.add(this.stack.pop());
        }
        node.setResults(results);
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Literal node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.stack.push(node);
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(FunctionLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.stack.push(node);
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ListLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // create collection of executed list content
        final List<Expression> executed = new ArrayList<Expression>(
            node.getContent().size());
        
        for (final Expression exp : node.getContent()) {
            // places executed expression on the stack
            if (!exp.visit(this)) {
                return false;
            }
            executed.add(this.stack.pop());
        }
        final ListLiteral result = new ListLiteral(node.getPosition(), executed);
        result.setUnique(node.getUnique());
        this.stack.push(result);
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // store current ns and switch to new one
        final Namespace backup = this.nspace;
        
        // get namespace which is accessed here and has the current namespace as 
        // parent. 
        // lhs of access is guaranteed to be a VarAccess
        final VarAccess va = (VarAccess) node.getLhs();
        this.nspace = Namespace.forName(va.getIdentifier()).derive(this.nspace);
        
        // execute expression and restore old namespace
        if (!node.getRhs().visit(this)) {
            return false;
        }
        this.nspace = backup;
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Native node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        node.execute(this.stack, this.nspace, this);
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Assignment node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // result of assignment is the result of the assigned expression
        if (!node.getExpression().visit(this)) {
            return false;
        }
        
        final Declaration vd = new Declaration(node.getName().getPosition(), 
            node.getName(), this.stack.peek());
        vd.setPublic(node.isPublic());
        vd.setTemp(node.isTemp());
        
        if (vd.isPublic()) {
            Namespace.declarePublic(vd);
        } else {
            this.rootNs.declare(vd);
        }
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(OperatorCall node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!this.visit((Call) node)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Call node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // this will push the function call onto the stack
        if (!node.getLhs().visit(this)) {
            return false;
        }
        
        final FunctionLiteral func = (FunctionLiteral) this.stack.pop();
        
        this.enter();
        final Iterator<Expression> actualIt = node.getRhs().getContent().iterator();
        final Iterator<Declaration> formalIt = func.getFormal().iterator();
        while (formalIt.hasNext()) {
            final Declaration formal = formalIt.next();
            final Expression actual = actualIt.next();
            
            // execute actual parameter
            if (!actual.visit(this)) {
                return false;
            }
            
            // declare result as local variable for this call
            final Expression result = this.stack.pop();
            final Declaration local = 
                new Declaration(actual.getPosition(), formal.getName(), result);
            this.nspace.declare(local);
        }

        if (!func.getBody().visit(this)) {
            return false;
        }
        this.leave();
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(VarAccess node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }

        final Declaration vd = this.nspace.tryResolve(
            node.getIdentifier(), 
            node.getUnique());
        if (!vd.getExpression().visit(this)) {
            return false;
        }
        
        return this.after(node) == CONTINUE;
    }
    
    

    @Override
    public boolean visit(Delete node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        int i = 0;
        for (final DeleteableIdentifier id: node.getIdentifiers()) {
            if (id.isGlobal()) {
                i += Namespace.deletePublic(id);
            } else {
                i += this.rootNs.delete(id);
            }
        }
        this.stack.push(new NumberLiteral(Position.NONE, i));
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        Namespace target = null;
        ResolvableIdentifier var = null;
        
        if (node.getAccess() instanceof VarAccess) {
            final VarAccess va = (VarAccess) node.getAccess();
            
            target = this.nspace;
            var = va.getIdentifier();
            
        } else if (node.getAccess() instanceof NamespaceAccess) {
            final NamespaceAccess nsa = (NamespaceAccess) node.getAccess();
            final VarAccess nsName = (VarAccess) nsa.getLhs();
            
            var = ((VarAccess) nsa.getRhs()).getIdentifier();
            target = Namespace.forName(nsName.getIdentifier());
        } else {
            throw new IllegalStateException("this should not be reachable");
        }
        
        final Collection<Declaration> decls = target.lookupAll(var);
        final StringBuilder b = new StringBuilder();
        for (final Declaration decl : decls) {
            if (decl.isNative()) {
                b.append("Native");
            } else if (decl.getExpression() instanceof FunctionLiteral) {
                b.append("Funktion: " + Unparser.toString(decl.getExpression()));
            } else {
                b.append("Wert: " + Unparser.toString(decl.getExpression()));
            }
            b.append(". Type: " + decl.getType().getName());
            b.append("\n");
        }
        this.stack.push(new StringLiteral(node.getPosition(), b.toString()));

        return this.after(node) == CONTINUE;
    }
}