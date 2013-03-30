package de.skuzzle.polly.core.parser.ast.declarations.types;

import de.skuzzle.polly.core.parser.ast.Identifier;

/**
 * Indicates a type that is unknown. 
 * 
 * @author Simon Taddiken
 */
public class MissingType extends Type {
    
    public MissingType(Identifier name) {
        super(name, false, true);
    }
}
