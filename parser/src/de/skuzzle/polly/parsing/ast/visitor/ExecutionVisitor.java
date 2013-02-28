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
import de.skuzzle.polly.parsing.util.LinkedStack;
import de.skuzzle.polly.parsing.util.Stack;



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
    public void visit(Root root) throws ASTTraversalException {
        this.before(root);
        
        final List<Literal> results = 
            new ArrayList<Literal>(root.getExpressions().size());
        
        for (final Expression exp : root.getExpressions()) {
            exp.visit(this);
            results.add(this.stack.pop());
        }
        root.setResults(results);
        
        this.after(root);
    }
    
    
    
    @Override
    public void visit(Literal literal) throws ASTTraversalException {
        this.before(literal);
        this.stack.push(literal);
        this.after(literal);
    }
    
    
    
    @Override
    public void visit(FunctionLiteral func) throws ASTTraversalException {
        this.before(func);
        this.stack.push(func);
        this.after(func);
    }
    
    
    
    @Override
    public void visit(ListLiteral list) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(list);
        
        // create collection of executed list content
        final List<Expression> executed = new ArrayList<Expression>(
                list.getContent().size());
        
        for (final Expression exp : list.getContent()) {
            // places executed expression on the stack
            exp.visit(this);
            executed.add(this.stack.pop());
        }
        final ListLiteral result = new ListLiteral(list.getPosition(), executed);
        result.setUnique(list.getUnique());
        this.stack.push(result);
        this.after(list);
    }
    
    
    
    @Override
    public void visit(NamespaceAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(access);
        
        // store current ns and switch to new one
        final Namespace backup = this.nspace;
        
        // get namespace which is accessed here and has the current namespace as 
        // parent. 
        // lhs of access is guaranteed to be a VarAccess
        final VarAccess va = (VarAccess) access.getLhs();
        this.nspace = Namespace.forName(va.getIdentifier()).derive(this.nspace);
        
        // execute expression and restore old namespace
        access.getRhs().visit(this);
        this.nspace = backup;
        
        this.after(access);
    }
    
    
    
    @Override
    public void visit(Native hc) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(hc);
        hc.execute(this.stack, this.nspace, this);
        this.after(hc);
    }
    
    
    
    @Override
    public void visit(Assignment assign) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(assign);
        
        // result of assignment is the result of the assigned expression
        assign.getExpression().visit(this);
        
        final Declaration vd = new Declaration(assign.getName().getPosition(), 
                assign.getName(), this.stack.peek());
        vd.setPublic(assign.isPublic());
        vd.setTemp(assign.isTemp());
        
        if (vd.isPublic()) {
            Namespace.declarePublic(vd);
        } else {
            this.rootNs.declare(vd);
        }
        
        this.after(assign);
    }
    
    
    
    @Override
    public void visit(OperatorCall call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(call);
        this.visit((Call) call);
        this.after(call);
    }
    
    
    
    @Override
    public void visit(Call call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(call);
        
        // this will push the function call onto the stack
        call.getLhs().visit(this);
        
        final FunctionLiteral func = (FunctionLiteral) this.stack.pop();
        
        this.enter();
        final Iterator<Expression> actualIt = call.getRhs().getContent().iterator();
        final Iterator<Declaration> formalIt = func.getFormal().iterator();
        while (formalIt.hasNext()) {
            final Declaration formal = formalIt.next();
            final Expression actual = actualIt.next();
            
            // execute actual parameter
            actual.visit(this);
            
            // declare result as local variable for this call
            final Expression result = this.stack.pop();
            final Declaration local = 
                new Declaration(actual.getPosition(), formal.getName(), result);
            this.nspace.declare(local);
        }

        func.getBody().visit(this);
        this.leave();
        
        this.after(call);
    }
    
    
    
    @Override
    public void visit(VarAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(access);

        final Declaration vd = this.nspace.tryResolve(
            access.getIdentifier(), 
            access.getUnique());
        vd.getExpression().visit(this);
        
        this.after(access);
    }
    
    

    @Override
    public void visit(Delete delete) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(delete);
        int i = 0;
        for (final DeleteableIdentifier id: delete.getIdentifiers()) {
            if (id.isGlobal()) {
                i += Namespace.deletePublic(id);
            } else {
                i += this.rootNs.delete(id);
            }
        }
        this.stack.push(new NumberLiteral(Position.NONE, i));
        this.after(delete);
    }
    
    
    
    @Override
    public void visit(Inspect inspect) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(inspect);
        
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

        this.after(inspect);
    }
}