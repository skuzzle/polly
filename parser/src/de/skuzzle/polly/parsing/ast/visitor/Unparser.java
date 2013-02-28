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
import de.skuzzle.polly.parsing.ast.expressions.Inspect;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.Delete.DeleteableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.LiteralFormatter;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
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
    public void before(Braced braced) throws ASTTraversalException {
        this.out.print("(");
    }
    
    
    
    @Override
    public void after(Braced braced) throws ASTTraversalException {
        this.out.print(")");
    }
    
    
    
    @Override
    public void before(Declaration decl) throws ASTTraversalException {
        decl.getType().getName().visit(this);
        this.out.print(" ");
        decl.getName().visit(this);
    }
    
    
    
    @Override
    public void visit(Root root) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(root);
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
        this.after(root);
    }
    
    
    
    @Override
    public void visit(FunctionLiteral func) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(func);
        
        this.out.print(func.format(this.literalFormatter));
        
        this.after(func);
    }
    
    
    
    @Override
    public void visit(Literal literal) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(literal);
        this.out.print(literal.format(this.literalFormatter));
        this.after(literal);
    }
    
    
    
    @Override
    public void visit(ListLiteral list) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(list);
        
        this.out.print(list.format(this.literalFormatter));
        
        this.after(list);
    }
    
    
    
    @Override
    public void visit(ProductLiteral product) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(product);
        
        final Iterator<Expression> it = product.getContent().iterator();
        while (it.hasNext()) {
            it.next().visit(this);
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        this.after(product);
    }
    
    
    
    @Override
    public void before(Identifier identifier) throws ASTTraversalException {
        if (identifier.wasEscaped()) {
            this.out.print("\\");
        }
        this.out.print(identifier.getId());
    }
    
    
    
    @Override
    public void before(ResolvableIdentifier id) throws ASTTraversalException {
        this.before((Identifier) id);
    }
    
    
    
    @Override
    public void visit(OperatorCall call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(call);
        
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
        this.after(call);
    }
    
    
    
    @Override
    public void visit(Call call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(call);
        
        call.getLhs().visit(this);
        this.out.print("(");
        call.getRhs().visit(this);
        this.out.print(")");
        
        this.after(call);
    }
    
    
    
    @Override
    public void visit(NamespaceAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(access);
        access.getLhs().visit(this);
        this.out.print(".");
        access.getRhs().visit(this);
        this.after(access);
    }
    
    
    
    @Override
    public void visit(VarAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(access);
        access.getIdentifier().visit(this);
        this.after(access);
    }
    
    
    
    @Override
    public void visit(Assignment assign) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.before(assign);
        assign.getExpression().visit(this);
        this.out.print("->");
        assign.getName().visit(this);
        this.after(assign);
    }

    
    
    @Override
    public void visit(Delete delete) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(delete);
        
        this.out.print("del(");
        final Iterator<DeleteableIdentifier> it = delete.getIdentifiers().iterator();
        while (it.hasNext()) {
            final DeleteableIdentifier next = it.next();
            if (next.isGlobal()) {
                this.out.print("public ");
            }
            next.visit(this);
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        this.out.print(")");
        this.after(delete);
    }
    
    
    
    @Override
    public void before(Inspect inspect) throws ASTTraversalException {
        this.out.print("inspect(");
    }
    
    
    
    @Override
    public void after(Inspect inspect) throws ASTTraversalException {
        this.out.print(")");
    }
}
