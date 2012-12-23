package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


/**
 * Represents the access of a variable.
 * 
 * @author Simon Taddiken
 */
public class VarAccess extends Expression {

    private static final long serialVersionUID = 1L;
    
    private ResolvableIdentifier identifier;
    
    
    public VarAccess(Position position, ResolvableIdentifier identifier) {
        super(position);
        if (identifier == null) {
            throw new NullPointerException("identifier is null");
        }
        this.identifier = identifier;
    }
    
    
    
    @Override
    public <T extends Node> void replaceChild(T current, T newChild) {
        if (current == this.identifier) {
            this.identifier = (ResolvableIdentifier) newChild;
        } else {
            super.replaceChild(current, newChild);
        }
    }
    
    
    
    public Declaration selectDeclaration() {
        for (final Declaration decl : this.identifier.getDeclarations()) {
            if (Type.unify(this.getUnique(), decl.getType(), false)) {
                final VarDeclaration declX = (VarDeclaration) decl;
                declX.getExpression().setUnique(this.getUnique());
                return decl;
            }
        }
        throw new IllegalStateException("no matchin decl");
    }
    
    
    
    /**
     * Gets the name of the variable that is accessed.
     * 
     * @return The variables name.
     */
    public ResolvableIdentifier getIdentifier() {
        return this.identifier;
    }

    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitVarAccess(this);
    }
    
    
    
    @Override
    public String toString() {
        return "[VarAccess: " + this.identifier + ", " + super.toString() + "]";
    }
}
