package de.skuzzle.polly.core.parser.ast.visitor;

import java.io.PrintStream;

import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.core.parser.util.PreOrderDotBuilder;



public class ASTVisualizer extends DepthFirstVisitor {

    private PreOrderDotBuilder dotBuilder;
    
    
    public void visualize(Node root, PrintStream out) throws ASTTraversalException {
        this.dotBuilder = new PreOrderDotBuilder(out, false);
        root.visit(this);
        this.dotBuilder.finish();
    }

    
    
    
    private void printExpression(String label, Expression exp) {
        final StringBuilder typesBuilder = new StringBuilder();
        for (final Type t : exp.getTypes()) {
            typesBuilder.append(t);
            typesBuilder.append("\\n");
        }
        this.dotBuilder.printNode(exp, 
            label,
            "Unique: " + exp.getUnique(), 
            "Types:\\n" + typesBuilder.toString(),
            exp.getPosition().toString());
    }
    


    @Override
    public int before(OperatorCall call) throws ASTTraversalException {
        this.printExpression("OperatorCall: " + call.getOperator().getId(), call);
        return CONTINUE;
    }



    @Override
    public int before(Call call) throws ASTTraversalException {
        this.printExpression("Call", call);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Literal literal) throws ASTTraversalException {
        this.printExpression(literal.toString(), literal);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ProductLiteral product) throws ASTTraversalException {
        this.printExpression("Product", product);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(NamespaceAccess access) throws ASTTraversalException {
        this.printExpression("Access", access);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Assignment assign) throws ASTTraversalException {
        this.printExpression("Assignment", assign);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(FunctionLiteral func) throws ASTTraversalException {
        this.printExpression("Function", func);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Native hc) throws ASTTraversalException {
        this.printExpression("Native", hc);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Identifier identifier) throws ASTTraversalException {
        this.dotBuilder.printNode(identifier, identifier.getId(),
            identifier.getPosition().toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ListLiteral list) throws ASTTraversalException {
        this.printExpression("List", list);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ResolvableIdentifier id) throws ASTTraversalException {
        this.dotBuilder.printNode(id.getId(), id.getPosition().toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Delete delete) throws ASTTraversalException {
        this.printExpression("Delete", delete);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Declaration decl) throws ASTTraversalException {
        this.dotBuilder.printNode(decl, 
            "Declaration: " + decl.getName().getId(),
            "Unique: " + decl.getType());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Root root) throws ASTTraversalException {
        this.dotBuilder.printNode(root, "Root", root.getPosition().toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(VarAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.printExpression("VarAccess: " + access.getIdentifier().getId(), access);
        return this.before(access) == CONTINUE;
    }
    
    
    
    @Override
    public int after(NamespaceAccess access) throws ASTTraversalException {
        this.dotBuilder.pop(access);
        return CONTINUE;
    }



    @Override
    public int after(Assignment assign) throws ASTTraversalException {
        this.dotBuilder.pop(assign);
        return CONTINUE;
    }



    @Override
    public int after(Call call) throws ASTTraversalException {
        this.dotBuilder.pop(call);
        return CONTINUE;
    }
    
    

    @Override
    public int after(FunctionLiteral func) throws ASTTraversalException {
        this.dotBuilder.pop(func);
        return CONTINUE;
    }

    
    
    @Override
    public int after(ProductLiteral product) throws ASTTraversalException {
        this.dotBuilder.pop(product);
        return CONTINUE;
    }
    


    @Override
    public int after(Native nat) throws ASTTraversalException {
        this.dotBuilder.pop(nat);
        return CONTINUE;
    }



    @Override
    public int after(Identifier identifier) throws ASTTraversalException {
        this.dotBuilder.pop(identifier);
        return CONTINUE;
    }



    @Override
    public int after(ListLiteral list) throws ASTTraversalException {
        this.dotBuilder.pop(list);
        return CONTINUE;
    }



    @Override
    public int after(Literal literal) throws ASTTraversalException {
        this.dotBuilder.pop(literal);
        return CONTINUE;
    }



    @Override
    public int after(OperatorCall call) throws ASTTraversalException {
        this.dotBuilder.pop(call);
        return CONTINUE;
    }



    @Override
    public int after(ResolvableIdentifier id) throws ASTTraversalException {
        this.dotBuilder.pop(id);
        return CONTINUE;
    }



    @Override
    public int after(Root root) throws ASTTraversalException {
        this.dotBuilder.pop(root);
        return CONTINUE;
    }



    @Override
    public int after(VarAccess access) throws ASTTraversalException {
        this.dotBuilder.pop(access);
        return CONTINUE;
    }



    @Override
    public int after(Declaration decl) throws ASTTraversalException {
        this.dotBuilder.pop(decl);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(Delete delete) throws ASTTraversalException {
        this.dotBuilder.pop(delete);
        return CONTINUE;
    }
}
