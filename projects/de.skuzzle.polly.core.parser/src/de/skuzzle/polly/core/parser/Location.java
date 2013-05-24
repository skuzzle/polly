package de.skuzzle.polly.core.parser;


/**
 * Interface for all classes that represent a position within a parsed input.
 * 
 * @author Simon Taddiken
 */
public interface Location {

    /**
     * Gets the position of this element within the parsed input.
     * 
     * @return The position.
     */
    public Position getPosition();
}
