package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.lang.Function;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.tools.Equatable;

/**
 * Base class for declarations. They must have at least a name and a {@link Position}. 
 * During type checking, a declaration may get assigned a {@link Type}. Two declarations
 * are considered equal, if their names are equal and their types a compatible as 
 * determined by {@link Type#check(Type)}.
 *   
 * @author Simon Taddiken
 */
public abstract class Declaration extends Node implements Comparable<Declaration> {


    private static final long serialVersionUID = 1L;
    
    private final Identifier name;
    private boolean isPublic;
    private boolean isTemp;
    private boolean mustCopy;
    private boolean primitive;
    
    
    public Declaration(Position position, Identifier name) {
        super(position);
        this.name = name;
    }
    
    
    
    public boolean mustCopy() {
        return this.mustCopy;
    }


    
    public void setMustCopy(boolean mustCopy) {
        this.mustCopy = mustCopy;
    }


    
    public abstract Type getType();
    
    
    
    public Identifier getName() {
        return this.name;
    }


    
    public boolean isPublic() {
        return this.isPublic;
    }


    
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
    public boolean isPrimitive() {
        return this.primitive;
    }
    
    
    
    /**
     * Sets whether this is a primitive declaration. Primitive delcarations are those, 
     * that are hardcoded into polly (mostly created by {@link Function} subclasses). 
     * Those declarations are not stored to file.
     * 
     * @param primitive Whether this is a primitive declaration.
     */
    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Declaration.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final Declaration other = (Declaration) o;
        return this.name.equals(other.name) && this.getType().check(other.getType());
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