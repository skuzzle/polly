package de.skuzzle.polly.dom;


public class ASTVisitor {

    /** Whether to continue traversal */
    public final static int PROCESS_CONTINUE = 0;
    
    /** Aborts traversal */
    public final static int PROCESS_ABORT = 1;
    
    /** Skips the current sub tree */
    public final static int PROCESS_SKIP = 2;
    
    
    
    public boolean shouldVisitNormalNames;
    public boolean shouldVisitQualifiedNames;
    public boolean shouldVisitBraced;
    public boolean shouldVisitProducts;
    public boolean shouldVisitIdExpressions;
    public boolean shouldVisitCalls;
    public boolean shouldVisitBinaryExpressions;
    public boolean shouldVisitOperators;
    public boolean shouldVisitUnaryExpressions;
    public boolean shouldVisitStringLiterals;
    public boolean shouldVisitParameters;
    public boolean shouldVisitFunctions;
    
    
    
    public ASTVisitor() { }
    
    public ASTVisitor(boolean visitAll) {
        this.shouldVisitNormalNames = visitAll;
        this.shouldVisitQualifiedNames = visitAll;
        this.shouldVisitBraced = visitAll;
        this.shouldVisitProducts = visitAll;
        this.shouldVisitIdExpressions = visitAll;
        this.shouldVisitCalls = visitAll;
        this.shouldVisitBinaryExpressions = visitAll;
        this.shouldVisitOperators = visitAll;
        this.shouldVisitUnaryExpressions = visitAll;
        this.shouldVisitStringLiterals = visitAll;
        this.shouldVisitParameters = visitAll;
        this.shouldVisitFunctions = visitAll;
    }
    
    
    
    public int visit(ASTQualifiedName node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTQualifiedName node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    
    public int visit(ASTName node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTName node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTProductExpression node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTProductExpression node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTBracedExpression node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTBracedExpression node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTIdExpression node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTIdExpression node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTCallExpression node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTCallExpression node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTBinaryExpression node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTBinaryExpression node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    
    public int visit(ASTOperator node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTOperator node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTUnaryExpression node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTUnaryExpression node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTStringLiteral node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTStringLiteral node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTParameter node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTParameter node) {
        return PROCESS_CONTINUE;
    }
    
    
    
    public int visit(ASTFunctionExpression node) {
        return PROCESS_CONTINUE;
    }
    
    public int leave(ASTFunctionExpression node) {
        return PROCESS_CONTINUE;
    }
}