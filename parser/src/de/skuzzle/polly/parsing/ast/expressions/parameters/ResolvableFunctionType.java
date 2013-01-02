package de.skuzzle.polly.parsing.ast.expressions.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class ResolvableFunctionType implements ResolvableType {

    private final ResolvableType resultType;
    private final Collection<ResolvableType> siganture;
    
    
    public ResolvableFunctionType(ResolvableType resultType, 
        Collection<ResolvableType> signature) {
        this.resultType = resultType;
        this.siganture = signature;
    }
    
    
    
    @Override
    public Type resolve() throws ASTTraversalException {
        final Type result = this.resultType.resolve();
        final List<Type> signature = new ArrayList<Type>(this.siganture.size());
        for (final ResolvableType type : this.siganture) {
            signature.add(type.resolve());
        }
        return new MapTypeConstructor(new ProductTypeConstructor(signature), result);
    }
    
    
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("\\(");
        final Iterator<ResolvableType> it = this.siganture.iterator();
        while (it.hasNext()) {
            b.append(it.next().toString());
            b.append(" ");
        }
        b.append("-> ");
        b.append(this.resultType.toString());
        b.append(")");
        return b.toString();
    }
}
