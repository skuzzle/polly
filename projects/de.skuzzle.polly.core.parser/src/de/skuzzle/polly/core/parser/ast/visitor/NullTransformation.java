package de.skuzzle.polly.core.parser.ast.visitor;

import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Braced;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
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


public class NullTransformation implements Transformation {

    @Override
    public Identifier transformIdentifier(Identifier identifier)
            throws ASTTraversalException {
        return identifier;
    }

    
    
    @Override
    public Root transformRoot(Root root) throws ASTTraversalException {
        root.setCommand(root.getCommand().transform(this));
        return null;
    }

    @Override
    public Declaration transformDeclaration(Declaration declaration)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformAssignment(Assignment assignment)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformBraced(Braced braced) throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformCall(Call call) throws ASTTraversalException {
        return null;
    }

    @Override
    public Node transformDelete(Delete delete) throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformAccess(NamespaceAccess namespaceAccess)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformNative(Native native1) throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformOperatorCall(OperatorCall operatorCall)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformVarAccess(VarAccess varAccess)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformBoolean(BooleanLiteral booleanLiteral) {
        return null;
    }

    @Override
    public Expression transformString(ChannelLiteral channelLiteral)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformDate(DateLiteral dateLiteral) throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformFunction(FunctionLiteral functionLiteral)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformList(ListLiteral listLiteral) throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformNumber(NumberLiteral numberLiteral)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformProduct(ProductLiteral productLiteral)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformString(StringLiteral stringLiteral)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformTimeSpan(TimespanLiteral timespanLiteral)
        throws ASTTraversalException {
        return null;
    }

    @Override
    public Expression transformUser(UserLiteral userLiteral) throws ASTTraversalException {
        return null;
    }



    @Override
    public HelpLiteral transformHelp(HelpLiteral helpLiteral) {
        return null;
    }



    @Override
    public Inspect transformInspect(Inspect inspect) {
        return null;
    }
}
