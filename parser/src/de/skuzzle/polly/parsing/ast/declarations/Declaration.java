package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.lang.Function;
import de.skuzzle.polly.tools.Equatable;

/**
 * Base class for declarations. They must have at least a name and a {@link Position}. 
 * During type checking, a declaration may get assigned a {@link Type}. Two declarations
 * are considered equal, if their names are equal and their types a compatible as 
 * determined by {@link Type#equals(Object)}.
 *   
 * @author Simon Taddiken
 */
public abstract class Declaration extends Node implements Comparable<Declaration> {

    private static final long serialVersionUID = 1L;
    
    private final Identifier name;
    private boolean isPublic;
    private boolean isTemp;
    private boolean mustCopy;
    private boolean isNative;
    
    
    public Declaration(Position position, Identifier name) {
        super(position);
        this.name = name;
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
    public abstract Type getType();
    
    
    
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
}