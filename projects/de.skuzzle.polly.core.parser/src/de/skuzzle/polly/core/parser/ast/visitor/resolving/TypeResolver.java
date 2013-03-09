package de.skuzzle.polly.core.parser.ast.visitor.resolving;

import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;


public final class TypeResolver {

    
    public final static void resolveAST(Node root, Namespace namespace) 
            throws ASTTraversalException {
        
        final FirstPassTypeResolver fptr = new FirstPassTypeResolver(namespace);
        root.visit(fptr);
        
        final SecondPassTypeResolver sptr = new SecondPassTypeResolver(fptr);
        root.visit(sptr);
    }
    
    
    
    
    private TypeResolver() {}
}