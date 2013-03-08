package de.skuzzle.polly.parsing.ast.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.Delete.DeleteableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.Inspect;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.StringLiteral;
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
    public boolean visit(Root root) throws ASTTraversalException {
        switch (this.before(root)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        final List<Literal> results = 
            new ArrayList<Literal>(root.getExpressions().size());
        
        for (final Expression exp : root.getExpressions()) {
            if (!exp.visit(this)) {
                return false;
            }
            results.add(this.stack.pop());
        }
        root.setResults(results);
        
        return this.after(root) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Literal literal) throws ASTTraversalException {
        switch (this.before(literal)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.stack.push(literal);
        return this.after(literal) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(FunctionLiteral func) throws ASTTraversalException {
        switch (this.before(func)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.stack.push(func);
        return this.after(func) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ListLiteral list) throws ASTTraversalException {
        switch (this.before(list)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // create collection of executed list content
        final List<Expression> executed = new ArrayList<Expression>(
                list.getContent().size());
        
        for (final Expression exp : list.getContent()) {
            // places executed expression on the stack
            if (!exp.visit(this)) {
                return false;
            }
            executed.add(this.stack.pop());
        }
        final ListLiteral result = new ListLiteral(list.getPosition(), executed);
        result.setUnique(list.getUnique());
        this.stack.push(result);
        return this.after(list) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // store current ns and switch to new one
        final Namespace backup = this.nspace;
        
        // get namespace which is accessed here and has the current namespace as 
        // parent. 
        // lhs of access is guaranteed to be a VarAccess
        final VarAccess va = (VarAccess) access.getLhs();
        this.nspace = Namespace.forName(va.getIdentifier()).derive(this.nspace);
        
        // execute expression and restore old namespace
        if (!access.getRhs().visit(this)) {
            return false;
        }
        this.nspace = backup;
        
        return this.after(access) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Native hc) throws ASTTraversalException {
        switch (this.before(hc)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        hc.execute(this.stack, this.nspace, this);
        return this.after(hc) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Assignment assign) throws ASTTraversalException {
        switch (this.before(assign)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // result of assignment is the result of the assigned expression
        if (!assign.getExpression().visit(this)) {
            return false;
        }
        
        final Declaration vd = new Declaration(assign.getName().getPosition(), 
                assign.getName(), this.stack.peek());
        vd.setPublic(assign.isPublic());
        vd.setTemp(assign.isTemp());
        
        if (vd.isPublic()) {
            Namespace.declarePublic(vd);
        } else {
            this.rootNs.declare(vd);
        }
        
        return this.after(assign) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(OperatorCall call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!this.visit((Call) call)) {
            return false;
        }
        return this.after(call) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Call call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // this will push the function call onto the stack
        if (!call.getLhs().visit(this)) {
            return false;
        }
        
        final FunctionLiteral func = (FunctionLiteral) this.stack.pop();
        
        this.enter();
        final Iterator<Expression> actualIt = call.getRhs().getContent().iterator();
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
        
        return this.after(call) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(VarAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }

        final Declaration vd = this.nspace.tryResolve(
            access.getIdentifier(), 
            access.getUnique());
        if (!vd.getExpression().visit(this)) {
            return false;
        }
        
        return this.after(access) == CONTINUE;
    }
    
    

    @Override
    public boolean visit(Delete delete) throws ASTTraversalException {
        switch (this.before(delete)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        int i = 0;
        for (final DeleteableIdentifier id: delete.getIdentifiers()) {
            if (id.isGlobal()) {
                i += Namespace.deletePublic(id);
            } else {
                i += this.rootNs.delete(id);
            }
        }
        this.stack.push(new NumberLiteral(Position.NONE, i));
        return this.after(delete) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect inspect) throws ASTTraversalException {
        switch (this.before(inspect)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        Namespace target = null;
        ResolvableIdentifier var = null;
        
        if (inspect.getAccess() instanceof VarAccess) {
            final VarAccess va = (VarAccess) inspect.getAccess();
            
            target = this.nspace;
            var = va.getIdentifier();
            
        } else if (inspect.getAccess() instanceof NamespaceAccess) {
            final NamespaceAccess nsa = (NamespaceAccess) inspect.getAccess();
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
        this.stack.push(new StringLiteral(inspect.getPosition(), b.toString()));

        return this.after(inspect) == CONTINUE;
    }
}