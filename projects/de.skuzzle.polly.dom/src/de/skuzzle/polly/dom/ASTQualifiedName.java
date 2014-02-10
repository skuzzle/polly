package de.skuzzle.polly.dom;

import java.util.List;

import de.skuzzle.parser.dom.ASTNodeProperty;



public interface ASTQualifiedName extends ASTName {

    /** Property for child names of this qualified name */
    public final static ASTNodeProperty PART_OF_QUALIFIED_NAME = 
            new ASTNodeProperty("PART_OF_QUALIFIED_NAME");
    
    
    @Override
    public List<ASTName> getChildren();
    
    /**
     * Gets the names of this qualified name. The returned list is read-only and provides
     * random access to its elements.
     * 
     * @return Array of names.
     */
    public List<ASTName> getNames();
    
    /**
     * Adds a name to the end of this qualified name. If this name has a location and
     * the provided name has one too, the location of this node is set to span until
     * the end of the provided name.
     * 
     * @param name The name to add.
     */
    public void addName(ASTName name);
    
    /**
     * Gets the last name of this qualified name.
     * 
     * @return The last name.
     */
    public ASTName getLastName();
    
    @Override
    public ASTQualifiedName getOrigin();
    
    @Override
    public ASTQualifiedName deepOrigin();
    
    @Override
    public ASTQualifiedName copy();
}