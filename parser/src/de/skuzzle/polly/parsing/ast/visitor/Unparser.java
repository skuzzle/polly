package de.skuzzle.polly.parsing.ast.visitor;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.LiteralFormatter;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.ast.lang.Operator.OpType;
import de.skuzzle.polly.tools.streams.StringBuilderWriter;



public class Unparser extends DepthFirstVisitor {
    
    public static String toString(Node node) {
        return toString(node, LiteralFormatter.DEFAULT);
    }
    
    
    
    public static String toString(Node node, LiteralFormatter formatter) {
        final StringBuilderWriter sbw = new StringBuilderWriter();
        final Unparser unp = new Unparser(new PrintWriter(sbw), formatter);
        try {
            node.visit(unp);
        } catch (ASTTraversalException e) {
            e.printStackTrace();
        }
        return sbw.getBuilder().toString();
    }

    
    
    private final PrintWriter out;
    private final LiteralFormatter literalFormatter;
    

    
    public Unparser(PrintWriter out, LiteralFormatter formatter) {
        this.out = out;
        this.literalFormatter = formatter;
    }
    
    
    
    public Unparser(PrintWriter out) {
        this(out, LiteralFormatter.DEFAULT);
    }
    
    
    
    @Override
    public void beforeBraced(Braced braced) throws ASTTraversalException {
        this.out.print("(");
    }
    
    
    
    @Override
    public void afterBraced(Braced braced) throws ASTTraversalException {
        this.out.print(")");
    }
    
    
    
    @Override
    public void beforeDecl(Declaration decl) throws ASTTraversalException {
        decl.getType().getName().visit(this);
        this.out.print(" ");
        decl.getName().visit(this);
    }
    
    
    
    @Override
    public void visitRoot(Root root) throws ASTTraversalException {
        this.beforeRoot(root);
        this.out.print(":");
        root.getCommand().visit(this);
        
        if (!root.getExpressions().isEmpty()) {
            this.out.print(" ");
            final Iterator<Expression> it = root.getExpressions().iterator();
            while (it.hasNext()) {
                it.next().visit(this);
                if (it.hasNext()) {
                    this.out.print(" ");
                }
            }
        }
        this.afterRoot(root);
    }
    
    
    
    @Override
    public void visitFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeFunctionLiteral(func);
        
        this.out.print(func.format(this.literalFormatter));
        
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void visitLiteral(Literal literal) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeLiteral(literal);
        this.out.print(literal.format(this.literalFormatter));
        this.afterLiteral(literal);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeListLiteral(list);
        
        this.out.print(list.format(this.literalFormatter));
        
        this.afterListLiteral(list);
    }
    
    
    
    @Override
    public void visitProductLiteral(ProductLiteral product) throws ASTTraversalException {
        this.beforeProductLiteral(product);
        final Iterator<Expression> it = product.getContent().iterator();
        while (it.hasNext()) {
            it.next().visit(this);
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        this.afterProductLiteral(product);
    }
    
    
    
    @Override
    public void beforeIdentifier(Identifier identifier) throws ASTTraversalException {
        if (identifier.wasEscaped()) {
            this.out.print("\\");
        }
        this.out.print(identifier.getId());
    }
    
    
    
    @Override
    public void beforeResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        this.beforeIdentifier(id);
    }
    
    
    
    @Override
    public void beforeParameter(Parameter param) throws ASTTraversalException {
        if (param.getResolvabelType() != null) {
            this.out.print(param.getResolvabelType().toString());
        } else {
            this.out.print(param.getUnique().getName().getId());
        }
        this.out.print(" ");
        param.getName().visit(this);
    }
    
    
    
    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeOperatorCall(call);
        
        final List<Expression> content = call.getRhs().getContent();
        final Iterator<Expression> it = content.iterator();
        
        if (content.size() == 1) {
            if (call.isPostfix()) {
                it.next().visit(this);
                this.out.print(call.getOperator().getId());
            } else {
                this.out.print(call.getOperator().getId());
                it.next().visit(this);
            }
        } else if (content.size() == 2) {
            // HACK: special treatment for certain operators
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
        } else if (content.size() == 3) {
            if (call.getOperator() == OpType.IF) {
                this.out.print("if ");
                it.next().visit(this);
                this.out.print(" ? ");
                it.next().visit(this);
                this.out.print(" : ");
                it.next().visit(this);
            } else if (call.getOperator() == OpType.DOTDOT) {
                it.next().visit(this);
                this.out.print("..");
                it.next().visit(this);
                this.out.print("$");
                it.next().visit(this);
            }
        }
        this.afterOperatorCall(call);
    }
    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        this.beforeCall(call);
        
        call.getLhs().visit(this);
        this.out.print("(");
        call.getRhs().visit(this);
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
        this.beforeVarAccess(access);
        access.getIdentifier().visit(this);
        this.afterVarAccess(access);
    }
    
    
    
    @Override
    public void visitAssignment(Assignment assign) throws ASTTraversalException {
        this.beforeAssignment(assign);
        assign.getExpression().visit(this);
        this.out.print("->");
        assign.getName().visit(this);
        this.afterAssignment(assign);
    }

    
    
    @Override
    public void visitDelete(Delete delete) throws ASTTraversalException {
        this.beforeDelete(delete);
        this.out.print("del(");
        final Iterator<Identifier> it = delete.getIdentifiers().iterator();
        while (it.hasNext()) {
            it.next().visit(this);
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        this.out.print(")");
        this.afterDelete(delete);
    }
}
