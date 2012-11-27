package de.skuzzle.polly.parsing.ast.visitor;

import java.io.PrintStream;
import java.util.Iterator;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.FunctionParameter;
import de.skuzzle.polly.parsing.ast.declarations.ListParameter;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.LiteralFormatter;
import de.skuzzle.polly.parsing.ast.operators.Operator.OpType;


public class Unparser extends DepthFirstVisitor {

    private final PrintStream out;
    private final LiteralFormatter literalFormatter;
    

    
    public Unparser(PrintStream out, LiteralFormatter formatter) {
        this.out = out;
        this.literalFormatter = formatter;
    }
    
    
    
    public Unparser(PrintStream out) {
        this(out, LiteralFormatter.DEFAULT);
    }
    
    
    
    @Override
    public void beforeRoot(Root root) throws ASTTraversalException {
        this.out.print(":");
        this.out.print(root.getCommand());
        if (!root.getExpressions().isEmpty()) {
            this.out.print(" ");
        }
    }
    
    
    
    @Override
    public void visitFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        this.beforeFunctionLiteral(func);
        this.out.print("\\(");
        final Iterator<Parameter> it = func.getFormal().iterator();
        while (it.hasNext()) {
            final Parameter formal = it.next();
            formal.visit(this);
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        this.out.print(":");
        func.getExpression().visit(this);
        this.out.print(")");
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void beforeLiteral(Literal literal) throws ASTTraversalException {
        this.out.print(literal.format(this.literalFormatter));
    }
    
    
    
    @Override
    public void beforeIdentifier(Identifier identifier) throws ASTTraversalException {
        this.out.print(identifier.getId());
    }
    
    
    
    @Override
    public void beforeResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        this.out.print(id.getId());
    }
    
    
    
    @Override
    public void beforeParameter(Parameter param) throws ASTTraversalException {
        param.getTypeName().visit(this);
        this.out.print(" ");
        param.getName().visit(this);
    }
    
    
    
    @Override
    public void beforeListParameter(ListParameter param) throws ASTTraversalException {
        param.getMainTypeName().visit(this);
        this.out.print("<");
        param.getType().getTypeName().visit(this);
        this.out.print("> ");
        param.getName().visit(this);
    }
    
    
    
    @Override
    public void beforeFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        this.out.print("\\(");
        final Iterator<ResolvableIdentifier> it = param.getSignature().iterator();
        while (it.hasNext()) {
            final ResolvableIdentifier next = it.next();
            next.visit(this);
            if (it.hasNext()) {
                this.out.print(" ");
            }
        }
        this.out.print(") ");
        param.getName().visit(this);
    }
    
    
    
    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeOperatorCall(call);
        final Iterator<Expression> it = call.getParameters().iterator();
        
        // HACK: add braces to ensure correct precedence
        if (call.getParameters().size() == 1) {
            if (call.isPostfix()) {
                this.out.print("(");
                it.next().visit(this);
                this.out.print(")");
                this.out.print(call.getOperator().getId());
            } else {
                this.out.print(call.getOperator().getId());
                this.out.print("(");
                it.next().visit(this);
                this.out.print(")");
            }
        } else if (call.getParameters().size() == 2) {
            // HACK: special treatment for certain operators
            this.out.print("(");
            if (call.getOperator() == OpType.INDEX) {
                it.next().visit(this);
                this.out.print("[");
                it.next().visit(this);
                this.out.print("]");
            } else {
                it.next().visit(this);
                this.out.print(call.getOperator().getId());
                it.next().visit(this);
            }
            this.out.print(")");
        }
        this.afterOperatorCall(call);
    }
    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        this.beforeCall(call);
        
        call.getLhs().visit(this);
        this.out.print("(");
        final Iterator<Expression> it = call.getParameters().iterator();
        while (it.hasNext()) {
            it.next().visit(this);
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        this.out.print(")");
        
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        this.beforeAccess(access);
        access.getLhs().visit(this);
        this.out.print(".");
        access.getRhs().visit(this);
        this.afterAccess(access);
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        access.getIdentifier().visit(this);
    }
    
    
    
    @Override
    public void visitAssignment(Assignment assign) throws ASTTraversalException {
        this.beforeAssignment(assign);
        this.out.print("(");
        assign.getExpression().visit(this);
        this.out.print("->");
        assign.getName().visit(this);
        this.out.print(")");
        this.afterAssignment(assign);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        this.beforeListLiteral(list);
        this.out.print("{");
        final Iterator<Expression> it = list.getContent().iterator();
        while (it.hasNext()) {
            it.next().visit(this);
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        this.out.print("}");
    }
}
