package de.skuzzle.polly.core.parser.ast.visitor;

import java.util.ArrayList;
import java.util.Collection;
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

public class CopyTransformation implements Transformation {

    @SuppressWarnings("unchecked")
    private final <T extends Node> List<T> transformList(Collection<T> nodes)
        throws ASTTraversalException {
        final List<T> result = new ArrayList<T>(nodes.size());

        for (final T node : nodes) {
            result.add((T) node.transform(this));
        }

        return result;
    }

    
    
    private <T extends Expression> T applyTypes(Expression source, T copy) {
        copy.setUnique(source.getUnique());
        copy.setTypes(source.getTypes());
        return copy;
    }
    


    @Override
    public Identifier transformIdentifier(Identifier node) throws ASTTraversalException {
        return new Identifier(node.getPosition(), node.getId());
    }



    @Override
    public Root transformRoot(Root node) throws ASTTraversalException {
        return new Root(node.getPosition(), node.getCommand().transform(this),
            this.transformList(node.getExpressions()), node.hasProblems());
    }



    @Override
    public Declaration transformDeclaration(Declaration node)
        throws ASTTraversalException {
        final Declaration result = new Declaration(node.getPosition(), node.getName()
            .transform(this), node.getExpression().transform(this));
        result.setNative(node.isNative());
        result.setPublic(result.isPublic());
        return result;
    }



    @Override
    public Expression transformAssignment(Assignment node) throws ASTTraversalException {
        return this.applyTypes(node, 
            new Assignment(
                node.getPosition(), 
                node.getExpression().transform(this),
                node.getName().transform(this), 
                node.isPublic(), 
                node.isTemp()));
    }



    @Override
    public Expression transformBraced(Braced node) throws ASTTraversalException {
        return this.applyTypes(node, 
            new Braced(node.getPosition(), 
                node.getExpression().transform(this)));
    }



    @Override
    public Expression transformCall(Call node) throws ASTTraversalException {
        return this.applyTypes(node, 
            new Call(
                node.getPosition(), 
                node.getLhs().transform(this), 
                node.getRhs().transform(this)));
    }



    @Override
    public Expression transformDelete(Delete node) throws ASTTraversalException {
        return this.applyTypes(node, 
            new Delete(node.getPosition(), 
            this.transformList(node.getIdentifiers())));
    }



    @Override
    public Expression transformAccess(NamespaceAccess node) throws ASTTraversalException {
        return this.applyTypes(node, 
            new NamespaceAccess(
                node.getPosition(), 
                node.getLhs().transform(this),
                node.getRhs().transform(this)));
    }



    @Override
    public Expression transformNative(Native node) throws ASTTraversalException {
        // XXX: native nodes are not copied
        return node;
    }



    @Override
    public Expression transformOperatorCall(OperatorCall node)
            throws ASTTraversalException {

        return this.applyTypes(node, 
            new OperatorCall(
                node.getPosition(), 
                node.getOperator(), 
                node.getRhs().transform(this).getContent(), 
                node.isPostfix()));
    }



    @Override
    public Expression transformVarAccess(VarAccess node) throws ASTTraversalException {
        return this.applyTypes(node, 
            new VarAccess(
                node.getPosition(), 
                node.getIdentifier().transform(this)));
    }



    @Override
    public Expression transformBoolean(BooleanLiteral node) {
        return new BooleanLiteral(node.getPosition(), node.getValue());
    }



    @Override
    public Expression transformString(ChannelLiteral node) throws ASTTraversalException {
        return new ChannelLiteral(node.getPosition(), node.getValue());
    }



    @Override
    public Expression transformDate(DateLiteral node) throws ASTTraversalException {
        return new DateLiteral(node.getPosition(), node.getValue());
    }



    @Override
    public Expression transformFunction(FunctionLiteral node)
            throws ASTTraversalException {
        return this.applyTypes(node, 
            new FunctionLiteral(
                node.getPosition(), 
                this.transformList(node.getFormal()), 
                node.getBody().transform(this)));
    }



    @Override
    public Expression transformList(ListLiteral node) throws ASTTraversalException {
        return this.applyTypes(node, 
            new ListLiteral(
                node.getPosition(), 
                this.transformList(node.getContent()), 
                node.getUnique()));
    }



    @Override
    public Expression transformNumber(NumberLiteral node) throws ASTTraversalException {
        return new NumberLiteral(node.getPosition(), node.getValue());
    }



    @Override
    public ProductLiteral transformProduct(ProductLiteral node) 
            throws ASTTraversalException {
        return this.applyTypes(node, 
            new ProductLiteral(
                node.getPosition(), 
                this.transformList(node.getContent())));
    }



    @Override
    public Expression transformString(StringLiteral node) throws ASTTraversalException {
        return new StringLiteral(node.getPosition(), node.getValue());
    }



    @Override
    public Expression transformTimeSpan(TimespanLiteral node)
            throws ASTTraversalException {
        return new TimespanLiteral(node.getPosition(), node.getSeconds());
    }



    @Override
    public Expression transformUser(UserLiteral node) throws ASTTraversalException {
        return new UserLiteral(node.getPosition(), node.getValue());
    }



    @Override
    public HelpLiteral transformHelp(HelpLiteral node) {
        return new HelpLiteral(node.getPosition());
    }



    @Override
    public Inspect transformInspect(Inspect node) throws ASTTraversalException {
        return new Inspect(node.getPosition(), node.getAccess().transform(this), 
            node.isGlobal());
    }



    @Override
    public ResolvableIdentifier transformIdentifier(ResolvableIdentifier node)
            throws ASTTraversalException {
        return new ResolvableIdentifier(node.getPosition(), node.getId(), 
            node.wasEscaped());
    }



    @Override
    public DeleteableIdentifier transformIdentifier(DeleteableIdentifier node) {
        return new DeleteableIdentifier(node, node.isGlobal());
    }
}
