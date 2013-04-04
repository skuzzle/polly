package de.skuzzle.polly.core.parser.ast.visitor;

import java.util.ArrayList;
import java.util.Collection;
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
import de.skuzzle.polly.core.parser.ast.expressions.Delete.DeleteableIdentifier;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ChannelLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.DateLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.HelpLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.UserLiteral;


public class NullTransform implements Transformation {

    
    private final <T extends Node> List<T> transformList(Collection<T> nodes) 
            throws ASTTraversalException {
        if (nodes == null) {
            return null;
        }
        
        final List<T> result = new ArrayList<T>(nodes.size());
        final Iterator<T> it = nodes.iterator();
        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
            final T next = (T) it.next().transform(this);
            if (next != null) {
                result.add(next);
            }
        }
        return result;
    }
    
    
    
    @Override
    public Identifier transformIdentifier(Identifier node) throws ASTTraversalException {
        return node;
    }

    
    
    @Override
    public ResolvableIdentifier transformIdentifier(ResolvableIdentifier node)
            throws ASTTraversalException {
        return node;
    }

    
    
    @Override
    public DeleteableIdentifier transformIdentifier(
            DeleteableIdentifier node) {
        return node;
    }
    
    

    @Override
    public Root transformRoot(Root node) throws ASTTraversalException {
        node.setCommand(node.getCommand().transform(this));
        node.setExpressions(this.transformList(node.getExpressions()));
        return node;
    }
    
    

    @Override
    public Declaration transformDeclaration(Declaration node)
            throws ASTTraversalException {
        return node;
    }

    
    
    @Override
    public Expression transformAssignment(Assignment node) throws ASTTraversalException {
        node.setExpression(node.getExpression().transform(this));
        node.setName(node.getName().transform(this));
        return node;
    }
    
    

    @Override
    public Expression transformBraced(Braced node) throws ASTTraversalException {
        node.setExpression(node.getExpression().transform(this));
        return node;
    }
    
    

    @Override
    public Expression transformCall(Call node) throws ASTTraversalException {
        node.setLhs(node.getLhs().transform(this));
        node.setRhs(node.getRhs().transform(this));
        return node;
    }
    
    

    @Override
    public Expression transformDelete(Delete node) throws ASTTraversalException {
        return node;
    }
    
    

    @Override
    public Expression transformAccess(NamespaceAccess node) throws ASTTraversalException {
        node.setLhs(node.getLhs().transform(this));
        node.setRhs(node.getRhs().transform(this));
        return node;
    }
    
    

    @Override
    public Expression transformNative(Native node) throws ASTTraversalException {
        return node;
    }
    
    

    @Override
    public Expression transformOperatorCall(OperatorCall node)
            throws ASTTraversalException {
        return this.transformCall(node);
    }
    
    

    @Override
    public Expression transformVarAccess(VarAccess node) throws ASTTraversalException {
        node.setIdentifier(node.getIdentifier().transform(this));
        return node;
    }
    
    

    @Override
    public Expression transformBoolean(BooleanLiteral node) {
        return node;
    }

    
    
    @Override
    public Expression transformString(ChannelLiteral node) throws ASTTraversalException {
        return node;
    }

    
    
    @Override
    public Expression transformDate(DateLiteral node) throws ASTTraversalException {
        return node;
    }
    
    

    @Override
    public Expression transformFunction(FunctionLiteral node)
            throws ASTTraversalException {
        node.setFormal(this.transformList(node.getFormal()));
        node.setBody(node.getBody().transform(this));
        return node;
    }
    
    

    @Override
    public Expression transformList(ListLiteral node) throws ASTTraversalException {
        node.setContent(this.transformList(node.getContent()));
        return node;
    }
    
    

    @Override
    public Expression transformNumber(NumberLiteral node) throws ASTTraversalException {
        return node;
    }

    
    
    @Override
    public ProductLiteral transformProduct(ProductLiteral node)
            throws ASTTraversalException {
        node.setContent(this.transformList(node.getContent()));
        return node;
    }
    
    

    @Override
    public Expression transformString(StringLiteral node) throws ASTTraversalException {
        return node;
    }

    
    
    @Override
    public Expression transformTimeSpan(TimespanLiteral node)
            throws ASTTraversalException {
        return node;
    }

    
    
    @Override
    public Expression transformUser(UserLiteral node) throws ASTTraversalException {
        return node;
    }

    
    
    @Override
    public HelpLiteral transformHelp(HelpLiteral node) throws ASTTraversalException {
        return node;
    }
    
    

    @Override
    public Inspect transformInspect(Inspect node) throws ASTTraversalException {
        return node;
    }
}
