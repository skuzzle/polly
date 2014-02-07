package de.skuzzle.polly.dom;

import java.util.Date;


public interface ASTTimespanLiteral extends ASTDateLiteral {

    /**
     * Gets the date resulting from adding this timespan to the current date.
     * @return The target date.
     */
    @Override
    public Date getValue();
    
    /**
     * Gets the date resulting from adding this timespan to the provided date.
     * @param base The base date.
     * @return The target date.
     */
    public Date getValue(Date base);
    
    /**
     * Returns the seconds in this timespan.
     * @return The seconds
     */
    public int getSeconds();
    
    @Override
    public ASTTimespanLiteral getOrigin();
    
    @Override
    public ASTTimespanLiteral deepOrigin();
    
    @Override
    public ASTTimespanLiteral copy();
}