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

    Identifier transformIdentifier(Identifier identifier) throws ASTTraversalException;

    Root transformRoot(Root root) throws ASTTraversalException;

    Declaration transformDeclaration(Declaration declaration) throws ASTTraversalException;

    Expression transformAssignment(Assignment assignment) throws ASTTraversalException;

    Expression transformBraced(Braced braced) throws ASTTraversalException;

    Expression transformCall(Call call) throws ASTTraversalException;

    Node transformDelete(Delete delete) throws ASTTraversalException;

    Expression transformAccess(NamespaceAccess namespaceAccess) throws ASTTraversalException;

    Expression transformNative(Native native1) throws ASTTraversalException;

    Expression transformOperatorCall(OperatorCall operatorCall) throws ASTTraversalException;

    Expression transformVarAccess(VarAccess varAccess) throws ASTTraversalException;

    Expression transformBoolean(BooleanLiteral booleanLiteral);

    Expression transformString(ChannelLiteral channelLiteral) throws ASTTraversalException;

    Expression transformDate(DateLiteral dateLiteral) throws ASTTraversalException;

    Expression transformFunction(FunctionLiteral functionLiteral) throws ASTTraversalException;

    Expression transformList(ListLiteral listLiteral) throws ASTTraversalException;

    Expression transformNumber(NumberLiteral numberLiteral) throws ASTTraversalException;

    Expression transformProduct(ProductLiteral productLiteral) throws ASTTraversalException;

    Expression transformString(StringLiteral stringLiteral) throws ASTTraversalException;

    Expression transformTimeSpan(TimespanLiteral timespanLiteral) throws ASTTraversalException;

    Expression transformUser(UserLiteral userLiteral) throws ASTTraversalException;

    HelpLiteral transformHelp(HelpLiteral helpLiteral);

    Inspect transformInspect(Inspect inspect);

}
