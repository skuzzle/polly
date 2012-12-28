package de.skuzzle.polly.parsing.ast.visitor;

import java.io.PrintStream;

import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.parsing.ast.expressions.parameters.FunctionParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.ListParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.util.ASTDotBuilder;


public class ExpASTVisualizer extends DepthFirstVisitor {
    
    
    private ASTDotBuilder dotBuilder;
    
    
    public void visualize(Node root, PrintStream out, Namespace ns) 
            throws ASTTraversalException {
        this.dotBuilder = new ASTDotBuilder(out);
        //this.dotBuilder.printNameSpace(ns);
        root.visit(this);
        this.dotBuilder.finish();
    }
    
    
    
    @Override
    public void visitRoot(Root root) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeRoot(root);
        this.dotBuilder.printNode(root, "Root", root.getPosition().toString());
        this.dotBuilder.printNode(root.getCommand(), "Name: " + root.getCommand(), 
                root.getCommand().getPosition().toString());
        this.dotBuilder.printEdge(root, root.getCommand(), "command");
        for (final Expression exp : root.getExpressions()) {
            exp.visit(this);
            this.dotBuilder.printEdge(root, exp, "param");
        }
        this.afterRoot(root);
    }
    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeCall(call);
        this.dotBuilder.printExpression("Call", call);
        call.getLhs().visit(this);
        call.getRhs().visit(this);
        
        this.dotBuilder.printEdge(call, call.getLhs(), "lhs");
        this.dotBuilder.printEdge(call, call.getRhs(), "rhs");
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeOperatorCall(call);
        this.dotBuilder.printExpression("OpCall: " + call.getOperator().getId(), call);
        call.getLhs().visit(this);
        call.getRhs().visit(this);
        
        this.dotBuilder.printEdge(call, call.getLhs(), "lhs");
        this.dotBuilder.printEdge(call, call.getRhs(), "rhs");
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeListLiteral(list);
        this.dotBuilder.printExpression("List", list);
        
        int i = 0;
        for (final Expression exp : list.getContent()) {
            exp.visit(this);
            this.dotBuilder.printEdge(list, exp, "" + (i++));
        }
        this.afterListLiteral(list);
    }
    
    
    
    @Override
    public void visitLiteral(Literal literal) throws ASTTraversalException {
        this.dotBuilder.printExpression(literal.toString(), literal);
    }
    
    
    
    @Override
    public void visitProductLiteral(ProductLiteral product) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeProductLiteral(product);
        this.dotBuilder.printExpression("Product", product);
        
        int i = 0;
        for (final Expression exp : product.getContent()) {
            exp.visit(this);
            this.dotBuilder.printEdge(product, exp, "" + (i++));
        }
        this.afterProductLiteral(product);
    }
    
    
    
    @Override
    public void visitFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeFunctionLiteral(func);
        
        this.dotBuilder.printExpression("Function", func);
        for (final Parameter param : func.getFormal()) {
            param.visit(this);
            this.dotBuilder.printEdge(func, param, "param");
        }
        func.getExpression().visit(this);
        this.dotBuilder.printEdge(func, func.getExpression(), "body");
        
        for (final Parameter param : func.getFormal()) {
            for (final VarAccess va : param.getUsage()) {
                this.dotBuilder.printEdge(param, va, "", "dotted", false);
            }
        }
        
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void visitParameter(Parameter param) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeParameter(param);
        this.dotBuilder.printExpression("Name: " + param.getName(), param);
        this.afterParameter(param);
    }
    
    
    
    @Override
    public void visitListParameter(ListParameter param) throws ASTTraversalException {
        this.visitParameter(param);
    }
    
    
    
    @Override
    public void visitFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        this.visitParameter(param);
    }
    
    
    
    @Override
    public void visitAssignment(Assignment assign) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeAssignment(assign);
        this.dotBuilder.printExpression("Assignment", assign);
        assign.getExpression().visit(this);
        this.dotBuilder.printNode(assign.getName(), "Name: " + assign.getName(), 
            assign.getName().getPosition().toString());
        this.dotBuilder.printEdge(assign, assign.getExpression(), "expresion");
        this.dotBuilder.printEdge(assign, assign.getName(), "to");
        this.afterAssignment(assign);
    }
    
    
    
    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {
        this.dotBuilder.printExpression("VarAccess: " + access.getIdentifier(), access);
        /*for (final Declaration decl : access.getIdentifier().getDeclarations()) {
            this.dotBuilder.printUsage(access, decl);
        }*/
    }
}
