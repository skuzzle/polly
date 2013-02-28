package de.skuzzle.test.polly.parsing.parser;

import de.skuzzle.polly.parsing.InputParser;


/**
 * Base class for all {@link InputParser} tests.
 * 
 * @author Simon Taddiken
 */
public abstract class AbstractParserTest {

    public InputParser obtain(String input) {
        return new InputParser(input);
    }
}