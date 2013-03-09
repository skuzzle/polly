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


public interface Transformation {

    Identifier transformIdentifier(Identifier node) throws ASTTraversalException;

    Root transformRoot(Root node) throws ASTTraversalException;

    Declaration transformDeclaration(Declaration node) throws ASTTraversalException;

    Expression transformAssignment(Assignment node) throws ASTTraversalException;

    Expression transformBraced(Braced node) throws ASTTraversalException;

    Expression transformCall(Call node) throws ASTTraversalException;

    Node transformDelete(Delete node) throws ASTTraversalException;

    Expression transformAccess(NamespaceAccess node) throws ASTTraversalException;

    Expression transformNative(Native node) throws ASTTraversalException;

    Expression transformOperatorCall(OperatorCall node) throws ASTTraversalException;

    Expression transformVarAccess(VarAccess node) throws ASTTraversalException;

    Expression transformBoolean(BooleanLiteral node);

    Expression transformString(ChannelLiteral node) throws ASTTraversalException;

    Expression transformDate(DateLiteral node) throws ASTTraversalException;

    Expression transformFunction(FunctionLiteral node) throws ASTTraversalException;

    Expression transformList(ListLiteral node) throws ASTTraversalException;

    Expression transformNumber(NumberLiteral node) throws ASTTraversalException;

    Expression transformProduct(ProductLiteral node) throws ASTTraversalException;

    Expression transformString(StringLiteral node) throws ASTTraversalException;

    Expression transformTimeSpan(TimespanLiteral node) throws ASTTraversalException;

    Expression transformUser(UserLiteral node) throws ASTTraversalException;

    HelpLiteral transformHelp(HelpLiteral node);

    Inspect transformInspect(Inspect node);
}
