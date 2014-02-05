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
    
    
    
    public ASTVisitor() { }
    
    public ASTVisitor(boolean visitAll) {
        this.shouldVisitNormalNames = visitAll;
        this.shouldVisitQualifiedNames = visitAll;
        this.shouldVisitBraced = visitAll;
        this.shouldVisitProducts = visitAll;
        this.shouldVisitIdExpressions = visitAll;
        this.shouldVisitCalls = visitAll;
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
}