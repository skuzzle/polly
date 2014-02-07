package de.skuzzle.polly.dom;

import java.util.Date;


public interface ASTDateLiteral extends ASTLiteral {

    @Override
    public Date getValue();
    
    /**
     * Sets the date value of this literal.
     * @param d The new date value.
     */
    public void setValue(Date d);
    
    @Override
    public ASTDateLiteral getOrigin();
    
    @Override
    public ASTDateLiteral deepOrigin();
    
    @Override
    public ASTDateLiteral copy();
}
