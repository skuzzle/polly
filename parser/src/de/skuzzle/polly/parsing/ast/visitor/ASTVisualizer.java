package de.skuzzle.polly.parsing.ast.visitor;

import java.io.PrintStream;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.parsing.util.PreOrderDotBuilder;



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
    public void before(OperatorCall call) throws ASTTraversalException {
        this.printExpression("OperatorCall: " + call.getOperator().getId(), call);
    }



    @Override
    public void before(Call call) throws ASTTraversalException {
        this.printExpression("Call", call);
    }
    
    
    
    @Override
    public void before(Literal literal) throws ASTTraversalException {
        this.printExpression(literal.toString(), literal);
    }
    
    
    
    @Override
    public void before(ProductLiteral product) throws ASTTraversalException {
        this.printExpression("Product", product);
    }
    
    
    
    @Override
    public void before(NamespaceAccess access) throws ASTTraversalException {
        this.printExpression("Access", access);
    }
    
    
    
    @Override
    public void before(Assignment assign) throws ASTTraversalException {
        this.printExpression("Assignment", assign);
    }
    
    
    
    @Override
    public void before(FunctionLiteral func) throws ASTTraversalException {
        this.printExpression("Function", func);
    }
    
    
    
    @Override
    public void before(Native hc) throws ASTTraversalException {
        this.printExpression("Native", hc);
    }
    
    
    
    @Override
    public void before(Identifier identifier) throws ASTTraversalException {
        this.dotBuilder.printNode(identifier, identifier.getId(),
            identifier.getPosition().toString());
    }
    
    
    
    @Override
    public void before(ListLiteral list) throws ASTTraversalException {
        this.printExpression("List", list);
    }
    
    
    
    @Override
    public void before(ResolvableIdentifier id) throws ASTTraversalException {
        this.dotBuilder.printNode(id.getId(), id.getPosition().toString());
    }
    
    
    
    @Override
    public void before(Delete delete) throws ASTTraversalException {
        this.printExpression("Delete", delete);
    }
    
    
    
    @Override
    public void before(Declaration decl) throws ASTTraversalException {
        this.dotBuilder.printNode(decl, 
            "Declaration: " + decl.getName().getId(),
            "Unique: " + decl.getType());
    }
    
    
    
    @Override
    public void before(Root root) throws ASTTraversalException {
        this.dotBuilder.printNode(root, "Root", root.getPosition().toString());
    }
    
    
    
    @Override
    public void visit(VarAccess access) throws ASTTraversalException {
        this.before(access);
        this.printExpression("VarAccess: " + access.getIdentifier().getId(), access);
        this.before(access);
    }
    
    
    
    @Override
    public void after(NamespaceAccess access) throws ASTTraversalException {
        this.dotBuilder.pop(access);
    }



    @Override
    public void after(Assignment assign) throws ASTTraversalException {
        this.dotBuilder.pop(assign);
    }



    @Override
    public void after(Call call) throws ASTTraversalException {
        this.dotBuilder.pop(call);
    }
    
    

    @Override
    public void after(FunctionLiteral func) throws ASTTraversalException {
        this.dotBuilder.pop(func);
    }

    
    
    @Override
    public void after(ProductLiteral product) throws ASTTraversalException {
        this.dotBuilder.pop(product);
    }
    


    @Override
    public void after(Native nat) throws ASTTraversalException {
        this.dotBuilder.pop(nat);
    }



    @Override
    public void after(Identifier identifier) throws ASTTraversalException {
        this.dotBuilder.pop(identifier);
    }



    @Override
    public void after(ListLiteral list) throws ASTTraversalException {
        this.dotBuilder.pop(list);
    }



    @Override
    public void after(Literal literal) throws ASTTraversalException {
        this.dotBuilder.pop(literal);
    }



    @Override
    public void after(OperatorCall call) throws ASTTraversalException {
        this.dotBuilder.pop(call);
    }



    @Override
    public void after(ResolvableIdentifier id) throws ASTTraversalException {
        this.dotBuilder.pop(id);
    }



    @Override
    public void after(Root root) throws ASTTraversalException {
        this.dotBuilder.pop(root);
    }



    @Override
    public void after(VarAccess access) throws ASTTraversalException {
        this.dotBuilder.pop(access);
    }



    @Override
    public void after(Declaration decl) throws ASTTraversalException {
        this.dotBuilder.pop(decl);
    }
    
    
    
    @Override
    public void after(Delete delete) throws ASTTraversalException {
        this.dotBuilder.pop(delete);
    }
}
