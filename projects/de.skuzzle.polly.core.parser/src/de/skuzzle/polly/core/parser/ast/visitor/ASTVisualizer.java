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
    public int before(OperatorCall node) throws ASTTraversalException {
        this.printExpression("OperatorCall: " + node.getOperator().getId(), node);
        return CONTINUE;
    }



    @Override
    public int before(Call node) throws ASTTraversalException {
        this.printExpression("Call", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Literal node) throws ASTTraversalException {
        this.printExpression(node.toString(), node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ProductLiteral node) throws ASTTraversalException {
        this.printExpression("Product", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(NamespaceAccess node) throws ASTTraversalException {
        this.printExpression("Access", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Assignment node) throws ASTTraversalException {
        this.printExpression("Assignment", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(FunctionLiteral node) throws ASTTraversalException {
        this.printExpression("Function", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Native node) throws ASTTraversalException {
        this.printExpression("Native", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Identifier node) throws ASTTraversalException {
        this.dotBuilder.printNode(node, node.getId(),
            node.getPosition().toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ListLiteral node) throws ASTTraversalException {
        this.printExpression("List", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ResolvableIdentifier node) throws ASTTraversalException {
        this.dotBuilder.printNode(node.getId(), node.getPosition().toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Delete node) throws ASTTraversalException {
        this.printExpression("Delete", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Declaration node) throws ASTTraversalException {
        this.dotBuilder.printNode(node, 
            "Declaration: " + node.getName().getId(),
            "Unique: " + node.getType());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Root node) throws ASTTraversalException {
        this.dotBuilder.printNode(node, "Root", node.getPosition().toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(VarAccess node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.printExpression("VarAccess: " + node.getIdentifier().getId(), node);
        return this.before(node) == CONTINUE;
    }
    
    
    
    @Override
    public int after(NamespaceAccess node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(Assignment node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(Call node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }
    
    

    @Override
    public int after(FunctionLiteral node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }

    
    
    @Override
    public int after(ProductLiteral node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }
    


    @Override
    public int after(Native node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(Identifier node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(ListLiteral node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(Literal node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(OperatorCall node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(ResolvableIdentifier node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(Root node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(VarAccess node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }



    @Override
    public int after(Declaration node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(Delete node) throws ASTTraversalException {
        this.dotBuilder.pop(node);
        return CONTINUE;
    }
}
