package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeUnifier;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class ProductLiteral extends ListLiteral {

    private static final long serialVersionUID = 1L;
    
    
    public ProductLiteral(Position position, List<Expression> content) {
        super(position, content);
    }

    
    
    @Override
    public void setUnique(Type unique) {
        if (!(unique instanceof ProductTypeConstructor)) {
            throw new IllegalArgumentException("no product type");
        }
        super.setUnique(unique);
        final ProductTypeConstructor ptc = (ProductTypeConstructor) unique;
        final Iterator<Type> typeIt = ptc.getTypes().iterator();
        for (final Expression exp : this.getContent()) {
            exp.setUnique(typeIt.next());
        }
    }
    
    
    
    @Override
    public void addType(Type type, TypeUnifier unifier) {
        if (!(type instanceof ProductTypeConstructor)) {
            throw new IllegalArgumentException("no product type");
        }
        super.addType(type, unifier);
        final ProductTypeConstructor ptc = (ProductTypeConstructor) type;
        final Iterator<Type> typeIt = ptc.getTypes().iterator();
        for (final Expression exp : this.getContent()) {
            exp.addType(typeIt.next(), unifier);
        }
    }
    
    
    
    @Override
    public void setTypes(Collection<Type> types, TypeUnifier unifier) {
        for (final Expression exp : this.getContent()) {
            exp.getTypes().clear();
        }
        super.setTypes(types, unifier);
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitProductLiteral(this);
    }
}
