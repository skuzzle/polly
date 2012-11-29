package de.skuzzle.polly.parsing;

import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.parsing.ast.visitor.TypeResolver;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.tools.streams.StringBuilderWriter;


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
    
    // TEST:
    public static void main(String[] args) throws UnsupportedEncodingException {
        String testMe = ":foo ((\\(Num x,\\(Num Num Num) y:y(x,10))->a)(5,\\(Num x, Num y : x * y))+a(17,\\(Num x, Num y:x+y)))->a \\(Num x, Num y: x-y)->a a";
        
        final Evaluator eval = new Evaluator(testMe, "ISO-8859-1");
        File decls = new File("decls");
        decls.mkdirs();
        Namespace.setDeclarationFolder(decls);
        
        final Namespace ns = Namespace.forName("me");
        
        eval.evaluate(ns);
        
        if (eval.errorOccurred()) {
            final ASTTraversalException e = eval.getLastError(); 
            System.out.println(e.getMessage());
            System.out.println(testMe);
            System.out.println(e.getPosition().errorIndicatorString());
        } else {
            System.out.println(eval.getRoot());
            System.out.println(eval.unparse());
        }
    }
    
    

    private final String input;
    private final String encoding;
    private Root lastResult;
    private ASTTraversalException lastError;
    
    
    
    public Evaluator(String input, String encoding) {
        this.input = input;
        this.encoding = encoding;
    }
    
    
    
    public void evaluate(Namespace namespace) throws UnsupportedEncodingException {
        try {
            final ExpInputParser parser = new ExpInputParser(this.input, this.encoding);
            final Root root = parser.parse();
            
            if (root == null) {
                return;
            }
            
            // resolve types
            final Visitor typeResolver = new TypeResolver(namespace);
            root.visit(typeResolver);
            
            final Visitor executor = new ExecutionVisitor(namespace);
            root.visit(executor);
            
            this.lastResult = root;
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
        if (this.errorOccurred()) {
            throw new IllegalStateException("no valid result available");
        }
        return this.lastResult;
    }
    
    
    
    public String unparse() {
        final Root r = this.getRoot();
        final StringBuilder b = new StringBuilder();
        final Visitor unparser = new Unparser(
            new PrintWriter(new StringBuilderWriter(b)));
        try {
            r.visit(unparser);
            return b.toString();
        } catch (ASTTraversalException e) {
            throw new RuntimeException(e);
        }
    }
}