package de.skuzzle.polly.core.parser.ast.visitor;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Braced;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Delete.DeleteableIdentifier;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.LiteralFormatter;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.core.parser.ast.lang.Operator.OpType;
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
    public int before(Braced braced) throws ASTTraversalException {
        this.out.print("(");
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(Braced braced) throws ASTTraversalException {
        this.out.print(")");
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Declaration decl) throws ASTTraversalException {
        decl.getType().getName().visit(this);
        this.out.print(" ");
        decl.getName().visit(this);
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Root root) throws ASTTraversalException {
        switch (this.before(root)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.out.print(":");
        if (!root.getCommand().visit(this)) {
            return false;
        }
        
        if (!root.getExpressions().isEmpty()) {
            this.out.print(" ");
            final Iterator<Expression> it = root.getExpressions().iterator();
            while (it.hasNext()) {
                if (!it.next().visit(this)) {
                    return false;
                }
                if (it.hasNext()) {
                    this.out.print(" ");
                }
            }
        }
        return this.after(root) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(FunctionLiteral func) throws ASTTraversalException {
        switch (this.before(func)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.out.print(func.format(this.literalFormatter));
        
        return this.after(func) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Literal literal) throws ASTTraversalException {
        switch (this.before(literal)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.out.print(literal.format(this.literalFormatter));
        return this.after(literal) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ListLiteral list) throws ASTTraversalException {
        switch (this.before(list)) {
        case SKIP: return true;
        case ABORT: return false;
        }        
        this.out.print(list.format(this.literalFormatter));
        
        return this.after(list) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ProductLiteral product) throws ASTTraversalException {
        switch (this.before(product)) {
        case SKIP: return true;
        case ABORT: return false;
        }        
        final Iterator<Expression> it = product.getContent().iterator();
        while (it.hasNext()) {
            if (!it.next().visit(this)) {
                return false;
            }
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        return this.after(product) == CONTINUE;
    }
    
    
    
    @Override
    public int before(Identifier identifier) throws ASTTraversalException {
        if (identifier.wasEscaped()) {
            this.out.print("\\");
        }
        this.out.print(identifier.getId());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ResolvableIdentifier id) throws ASTTraversalException {
        this.before((Identifier) id);
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(OperatorCall call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        final List<Expression> content = call.getRhs().getContent();
        final Iterator<Expression> it = content.iterator();
        
        if (content.size() == 1) {
            if (call.isPostfix()) {
                if (!it.next().visit(this)) {
                    return false;
                }
                this.out.print(call.getOperator().getId());
            } else {
                this.out.print(call.getOperator().getId());
                if (!it.next().visit(this)) {
                    return false;
                }
            }
        } else if (content.size() == 2) {
            // HACK: special treatment for certain operators
            if (call.getOperator() == OpType.INDEX) {
                if (!it.next().visit(this)) {
                    return false;
                }
                this.out.print("[");
                if (!it.next().visit(this)) {
                    return false;
                }
                this.out.print("]");
            } else {
                if (!it.next().visit(this)) {
                    return false;
                }
                this.out.print(call.getOperator().getId());
                if (!it.next().visit(this)) {
                    return false;
                }
            }
        } else if (content.size() == 3) {
            if (call.getOperator() == OpType.IF) {
                this.out.print("if ");
                if (!it.next().visit(this)) {
                    return false;
                }
                this.out.print(" ? ");
                if (!it.next().visit(this)) {
                    return false;
                }
                this.out.print(" : ");
                if (!it.next().visit(this)) {
                    return false;
                }
            } else if (call.getOperator() == OpType.DOTDOT) {
                if (!it.next().visit(this)) {
                    return false;
                }
                this.out.print("..");
                if (!it.next().visit(this)) {
                    return false;
                }
                this.out.print("$");
                if (!it.next().visit(this)) {
                    return false;
                }
            }
        }
        return this.after(call) == CONTINUE; 
    }
    
    
    
    @Override
    public boolean visit(Call call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        if (!call.getLhs().visit(this)) {
            return false;
        }
        this.out.print("(");
        if (!call.getRhs().visit(this)) {
            return false;
        }
        this.out.print(")");
        
        return this.after(call) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!access.getLhs().visit(this)) {
            return false;
        }
        this.out.print(".");
        if (!access.getRhs().visit(this)) {
            return false;
        }
        return this.after(access) == CONTINUE; 
    }
    
    
    
    @Override
    public boolean visit(VarAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!access.getIdentifier().visit(this)) {
            return false;
        }
        return this.after(access) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Assignment assign) throws ASTTraversalException {
        switch (this.before(assign)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!assign.getExpression().visit(this)) {
            return false;
        }
        this.out.print("->");
        if (!assign.getName().visit(this)) {
            return false;
        }
        return this.after(assign) == CONTINUE; 
    }

    
    
    @Override
    public boolean visit(Delete delete) throws ASTTraversalException {
        switch (this.before(delete)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.out.print("del(");
        final Iterator<DeleteableIdentifier> it = delete.getIdentifiers().iterator();
        while (it.hasNext()) {
            final DeleteableIdentifier next = it.next();
            if (next.isGlobal()) {
                this.out.print("public ");
            }
            if (!next.visit(this)) {
                return false;
            }
            if (it.hasNext()) {
                this.out.print(",");
            }
        }
        this.out.print(")");
        return this.after(delete) == CONTINUE;
    }
    
    
    
    @Override
    public int before(Inspect inspect) throws ASTTraversalException {
        this.out.print("inspect(");
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(Inspect inspect) throws ASTTraversalException {
        this.out.print(")");
        return CONTINUE;
    }
}
