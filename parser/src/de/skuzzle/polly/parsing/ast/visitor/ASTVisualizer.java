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
import de.skuzzle.polly.parsing.ast.expressions.parameters.FunctionParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.ListParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.util.DotBuilder;



public class ASTVisualizer extends DepthFirstVisitor {


    private DotBuilder dotBuilder;
    
    
    public void visualize(Node root, PrintStream out) throws ASTTraversalException {
        this.dotBuilder = new DotBuilder(out, false);
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
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.printExpression("OperatorCall: " + call.getOperator().getId(), call);
    }



    @Override
    public void beforeCall(Call call) throws ASTTraversalException {
        this.printExpression("Call", call);
    }
    
    
    
    @Override
    public void beforeLiteral(Literal literal) throws ASTTraversalException {
        this.printExpression(literal.toString(), literal);
    }
    
    
    
    @Override
    public void beforeProductLiteral(ProductLiteral product) throws ASTTraversalException {
        this.printExpression("Product", product);
    }
    
    
    
    @Override
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException {
        this.printExpression("Access", access);
    }
    
    
    
    @Override
    public void beforeAssignment(Assignment assign) throws ASTTraversalException {
        this.printExpression("Assignment", assign);
    }
    
    
    
    @Override
    public void beforeFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        this.printExpression("Function", func);
    }
    
    
    
    @Override
    public void beforeNative(Native hc) throws ASTTraversalException {
        this.printExpression("Native", hc);
    }
    
    
    
    @Override
    public void beforeIdentifier(Identifier identifier) throws ASTTraversalException {
        this.dotBuilder.printNode(identifier, identifier.getId(),
            identifier.getPosition().toString());
    }
    
    
    
    @Override
    public void beforeListLiteral(ListLiteral list) throws ASTTraversalException {
        this.printExpression("List", list);
    }
    
    
    
    @Override
    public void beforeResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        this.dotBuilder.printNode(id.getId(), id.getPosition().toString());
    }
    
    
    
    @Override
    public void beforeParameter(Parameter param) throws ASTTraversalException {
        this.printExpression("Parameter: " + param.getName().getId(), param);
    }
    
    
    
    @Override
    public void beforeListParameter(ListParameter param) throws ASTTraversalException {
        this.printExpression("Parameter: " + param.getName().getId(), param);
    }
    
    
    
    @Override
    public void beforeFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        this.printExpression("Parameter: " + param.getName().getId(), param);
    }
    
    
    
    @Override
    public void beforeDelete(Delete delete) throws ASTTraversalException {
        this.printExpression("Delete", delete);
    }
    
    
    
    @Override
    public void beforeDecl(Declaration decl) throws ASTTraversalException {
        this.dotBuilder.printNode(decl, 
            "Declaration: " + decl.getName().getId(),
            "Unique: " + decl.getType());
    }
    
    
    
    @Override
    public void beforeRoot(Root root) throws ASTTraversalException {
        this.dotBuilder.printNode(root, "Root", root.getPosition().toString());
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        this.beforeVarAccess(access);
        this.printExpression("VarAccess: " + access.getIdentifier().getId(), access);
        this.afterVarAccess(access);
    }
    
    
    
    @Override
    public void afterAccess(NamespaceAccess access) throws ASTTraversalException {
        this.dotBuilder.pop(access);
    }



    @Override
    public void afterAssignment(Assignment assign) throws ASTTraversalException {
        this.dotBuilder.pop(assign);
    }



    @Override
    public void afterCall(Call call) throws ASTTraversalException {
        this.dotBuilder.pop(call);
    }
    
    

    @Override
    public void afterFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        this.dotBuilder.pop(func);
    }

    
    
    @Override
    public void afterProductLiteral(ProductLiteral product) throws ASTTraversalException {
        this.dotBuilder.pop(product);
    }
    


    @Override
    public void afterNative(Native nat) throws ASTTraversalException {
        this.dotBuilder.pop(nat);
    }



    @Override
    public void afterIdentifier(Identifier identifier) throws ASTTraversalException {
        this.dotBuilder.pop(identifier);
    }



    @Override
    public void afterListLiteral(ListLiteral list) throws ASTTraversalException {
        this.dotBuilder.pop(list);
    }



    @Override
    public void afterLiteral(Literal literal) throws ASTTraversalException {
        this.dotBuilder.pop(literal);
    }



    @Override
    public void afterOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.dotBuilder.pop(call);
    }



    @Override
    public void afterParameter(Parameter param) throws ASTTraversalException {
        this.dotBuilder.pop(param);
    }



    @Override
    public void afterResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        this.dotBuilder.pop(id);
    }



    @Override
    public void afterRoot(Root root) throws ASTTraversalException {
        this.dotBuilder.pop(root);
    }



    @Override
    public void afterVarAccess(VarAccess access) throws ASTTraversalException {
        this.dotBuilder.pop(access);
    }



    @Override
    public void afterDecl(Declaration decl) throws ASTTraversalException {
        this.dotBuilder.pop(decl);
    }
    
    
    
    @Override
    public void afterDelete(Delete delete) throws ASTTraversalException {
        this.dotBuilder.pop(delete);
    }
    
    
    @Override
    public void afterListParameter(ListParameter param) throws ASTTraversalException {
        this.dotBuilder.pop(param);
    }
    
    
    
    @Override
    public void afterFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        this.dotBuilder.pop(param);
    }
}
