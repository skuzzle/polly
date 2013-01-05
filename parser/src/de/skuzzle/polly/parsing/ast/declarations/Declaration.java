package de.skuzzle.polly.parsing.ast.declarations;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Empty;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.lang.Function;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.tools.Equatable;

/**
 * Base class for declarations. They must have at least a name and a {@link Position}. 
 * During type checking, a declaration may get assigned a {@link Type}. Two declarations
 * are considered equal, if their names are equal and their types a compatible as 
 * determined by {@link Type#equals(Object)}.
 *   
 * @author Simon Taddiken
 */
public class Declaration extends Node implements Comparable<Declaration> {

    private static final long serialVersionUID = 1L;
    
    private final Identifier name;
    private boolean isPublic;
    private boolean isTemp;
    private boolean mustCopy;
    private boolean isNative;
    private final boolean isParameter;
    private final Expression expression;
    private final Collection<VarAccess> usage;
    
    
    
    public Declaration(Position position, Identifier name, Expression expression) {
        super(position);
        this.name = name;
        this.expression = expression;
        this.isParameter = false;
        this.usage = new ArrayList<VarAccess>();
    }
    
    
    
    /**
     * Creates a new declaration which represents a formal parameter of a function.
     * 
     * @param position Position within the source.
     * @param name Name of the parameter.
     * @param type The declared type of the parameter.
     */
    public Declaration(Position position, Identifier name, Type type) {
        super(position);
        this.name = name;
        this.expression = new Empty(type, position);
        this.isParameter = true;
        this.usage = new ArrayList<VarAccess>();
    }
    
    
    
    /**
     * Whether the declaration will be copied when being resolved by a {@link Namespace}.
     * 
     * @return Whether declaration will be copied.
     */
    public boolean mustCopy() {
        return this.mustCopy;
    }


    
    /**
     * Returns whether this declaration is accessed anywhere.
     * 
     * @return Whether this declaration is accessed anywhere.
     */
    public boolean isUnused() {
        return this.usage.isEmpty();
    }
    

    
    /**
     * Sets whether this declaration must be copied when being resolved.
     * 
     * @param mustCopy Whether this declaration must be copied when being resolved.
     */
    public void setMustCopy(boolean mustCopy) {
        this.mustCopy = mustCopy;
    }

    
    
    /**
     * Gets the type of this declaration.
     * 
     * @return The type.
     */
    public Type getType() {
        return this.expression.getUnique();
    }
    
    
    
    /**
     * Gets the declared expression.
     * 
     * @return The expression.
     */
    public Expression getExpression() {
        return this.expression;
    }
    
    

    
    /**
     * Gets the name of this declaration.
     * 
     * @return The name.
     */
    public Identifier getName() {
        return this.name;
    }


    
    /**
     * Gets whether this is a public declaration.
     * 
     * @return Whether this is a public declaration.
     */
    public boolean isPublic() {
        return this.isPublic;
    }


    
    /**
     * Sets whether this is a public declaration. This will only have a practical effect
     * when being set before this declaration is declared in a {@link Namespace}.
     * 
     * @param isPublic Whether this is a public declaration.
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }


    
    public boolean isTemp() {
        return this.isTemp;
    }


    
    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }
    
    
    
    /**
     * Whether this is a declaration of a formal function parameter.
     * 
     * @return Whether this is a declaration of a formal function parameter.
     */
    public boolean isParameter() {
        return this.isParameter;
    }
    
    
    
    /**
     * Gets whether this is a primitive delcaration.
     * 
     * @return Whether this is a primitive declaration.
     */
    public boolean isNative() {
        return this.isNative;
    }
    
    
    
    /**
     * Sets whether this is a native declaration. Native delcarations are those, 
     * that are hardcoded into polly (mostly created by {@link Function} subclasses). 
     * Those declarations are not stored to file.
     * 
     * @param isNative Whether this is a native declaration.
     */
    public void setNative(boolean isNative) {
        this.isNative = isNative;
    }
    
    
    
    /**
     * Gets all AST nodes that may have access to this declaration.
     * 
     * @return Collection of VarAccess's.
     */
    public Collection<VarAccess> getUsage() {
        return this.usage;
    }
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Declaration.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final Declaration other = (Declaration) o;
        return this.name.equals(other.name) && this.getType().equals(other.getType());
    }
    
    
    
    @Override
    public int compareTo(Declaration o) {
        // order by length, then lexically
        
        final String thisId = this.getName().getId();
        final String otherId = o.getName().getId();
        final int lengthComp = Integer.compare(thisId.length(), otherId.length());
        
        return lengthComp != 0 ? lengthComp : 
            this.name.getId().compareTo(o.getName().getId());
    }
    
    
    
    /**
     * Callback method which is called when a declaration is looked up.
     * 
     * @param access The {@link VarAccess} expression in the AST.
     */
    protected void onLookup(VarAccess access) {
        this.usage.add(access);
    }
    
    
    
    @Override
    public String toString() {
        return this.name + " [Type: " + this.getType() + "]";
    }



    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitDecl(this);
    }
}