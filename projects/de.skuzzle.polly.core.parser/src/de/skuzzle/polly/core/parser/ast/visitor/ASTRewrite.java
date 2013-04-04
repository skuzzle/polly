package de.skuzzle.polly.core.parser.ast.visitor;

import java.util.HashMap;
import java.util.Map;

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
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Delete.DeleteableIdentifier;
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

/**
 * This class can collect changes that are to be made on an abstract syntax tree and then 
 * perform all those changes in one pass.
 *  
 * @author Simon Taddiken
 */
public class ASTRewrite {

    private final Map<Node, Node> nodeMap;
    
    
    public ASTRewrite() {
        this.nodeMap = new HashMap<Node, Node>();
    }
    
    
    
    /**
     * Returns whether this rewriter contains any changes to be made.
     * 
     * @return Whether this rewriter contains any changes to be made.
     */
    public boolean hasChanges() {
        return !this.nodeMap.isEmpty();
    }
    
    
    
    /**
     * Notes the given source node to be replaced with the given target node. 
     * 
     * @param source The node to be replaced.
     * @param with The replacement.
     */
    public void rewrite(Node source, Node with) {
        this.nodeMap.put(source, with);
    }
    
    
    
    /**
     * Applies all collected replacement rules to the given (sub) tree and returns the
     * possibly new root. Nodes for which exists no replacement rule will be left 
     * unchanged in the AST. Thus this method will return the same node which was provided
     * as parameter if there is no replacement rule for it.
     * 
     * @param root The root node where to start node replacement.
     * @return The possibly new root created by replacements.
     * @throws ASTTraversalException If traversal of the AST fails for any reason.
     */
    @SuppressWarnings("unchecked")
    public <T extends Node> T apply(T root) throws ASTTraversalException {
        final Transformation rewriteTransform = new NullTransform() {
            
            @Override
            public Expression transformVarAccess(VarAccess node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformVarAccess(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformUser(UserLiteral node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformUser(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformTimeSpan(TimespanLiteral node)
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformTimeSpan(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformString(StringLiteral node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformString(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformString(ChannelLiteral node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformString(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Root transformRoot(Root node) throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformRoot(node) 
                    : (Root) rewrite;
            }
            
            
            
            @Override
            public ProductLiteral transformProduct(ProductLiteral node)
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformProduct(node) 
                    : (ProductLiteral) rewrite;
            }
            
            
            
            @Override
            public Expression transformOperatorCall(OperatorCall node)
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformOperatorCall(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformNumber(NumberLiteral node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformNumber(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformNative(Native node) throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformNative(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformList(ListLiteral node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformList(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Inspect transformInspect(Inspect node) throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformInspect(node) 
                    : (Inspect) rewrite;
            }
            
            
            
            @Override
            public DeleteableIdentifier transformIdentifier(DeleteableIdentifier node) {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformIdentifier(node) 
                    : (DeleteableIdentifier) rewrite;
            }
            
            
            
            @Override
            public ResolvableIdentifier transformIdentifier(ResolvableIdentifier node)
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformIdentifier(node) 
                    : (ResolvableIdentifier) rewrite;
            }
            
            
            
            @Override
            public Identifier transformIdentifier(Identifier node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformIdentifier(node) 
                    : (Identifier) rewrite;
            }
            
            
            
            @Override
            public HelpLiteral transformHelp(HelpLiteral node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformHelp(node) 
                    : (HelpLiteral) rewrite;
            }
            
            
            
            @Override
            public Expression transformFunction(FunctionLiteral node)
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformFunction(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformDelete(Delete node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformDelete(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Declaration transformDeclaration(Declaration node)
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformDeclaration(node) 
                    : (Declaration) rewrite;
            }
            
            
            
            @Override
            public Expression transformDate(DateLiteral node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformDate(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformCall(Call node) throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformCall(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformBraced(Braced node) throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformBraced(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformBoolean(BooleanLiteral node) {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformBoolean(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformAssignment(Assignment node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformAssignment(node) 
                    : (Expression) rewrite;
            }
            
            
            
            @Override
            public Expression transformAccess(NamespaceAccess node) 
                    throws ASTTraversalException {
                final Node rewrite = nodeMap.get(node);
                return rewrite == null 
                    ? super.transformAccess(node) 
                    : (Expression) rewrite;
            }
        };
        // HACK: using seconds pass to update parents
        return (T) root.transform(rewriteTransform).updateParents();
    }
}
