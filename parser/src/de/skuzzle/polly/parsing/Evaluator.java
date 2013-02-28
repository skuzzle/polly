package de.skuzzle.polly.parsing;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.DebugExecutionVisitor;
import de.skuzzle.polly.parsing.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.parsing.ast.visitor.ParentSetter;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.ast.visitor.resolving.TypeResolver;


/**
 * This is the main class for accessing the most often used parser feature: evaluating
 * an input String. It parses the String, resolves all types and executes the input in
 * the context of a provided {@link Namespace}. Evaluation may be either successful or
 * fail. In the latter case, you can retrieve the Exception that caused the fail using
 * {@link #getLastError()}. If no exception occurred, you may retrieve the result using
 * {@link #getRoot()}.  
 * 
 * @author Simon Taddiken
 */
public class Evaluator {
    
    public final static boolean DEBUG_MODE = false;
    
    private final static ExecutionVisitor getExecutor(Namespace rootNs, 
            Namespace workingNs) {
        if (DEBUG_MODE) {
            return new DebugExecutionVisitor(rootNs, workingNs);
        } else {
            return new ExecutionVisitor(rootNs, workingNs);
        }
    }

    private final String input;
    private final String encoding;
    private Root lastResult;
    private ASTTraversalException lastError;
    
    
    
    public Evaluator(String input, String encoding) throws UnsupportedEncodingException {
        if (!Charset.isSupported(encoding)) {
            throw new UnsupportedEncodingException(encoding);
        }
        this.input = input;
        this.encoding = encoding;
    }
    
    
    
    /**
     * Gets the input String which is parsed by this evaluator.
     * 
     * @return The input string.
     */
    public String getInput() {
        return this.input;
    }
    
    
    
    /**
     * Tries to evaluate the input that this instance was created with. Success of
     * evaluation can be queried using {@link #errorOccurred()}. If an error occurred,
     * the exception can be obtained using {@link #getLastError()}. If evaluation was
     * successful, the result can be obtained using {@link #getRoot()}. 
     * 
     * @param rootNs The namespace to which ne declarations will be stored.
     * @param workingNs The initial namespace to work with.
     */
    public void evaluate(Namespace rootNs, Namespace workingNs) {
        try {
            final InputParser parser = new InputParser(this.input, this.encoding);
            this.lastResult = parser.parse();
            
            if (this.lastResult == null) {
                return;
            }
            
            // set parent attributes for all nodes
            final Visitor parentSetter = new ParentSetter();
            this.lastResult.visit(parentSetter);
            
            // resolve types
            TypeResolver.resolveAST(this.lastResult, workingNs);
            
            final Visitor executor = getExecutor(rootNs, workingNs);
            this.lastResult.visit(executor);
            
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("This should not have happened", e);
        } catch (ASTTraversalException e) {
            this.lastError = e;
        }
    }
    
    
    
    public boolean errorOccurred() {
        return this.lastError != null;
    }
    
    
    
    public ASTTraversalException getLastError() {
        return this.lastError;
    }
    
    
    
    public Root getRoot() {
        /*if (this.errorOccurred()) {
            throw new IllegalStateException("no valid result available");
        }*/
        return this.lastResult;
    }
    
    
    
    public String unparse() {
        return Unparser.toString(this.getRoot());
    }
}