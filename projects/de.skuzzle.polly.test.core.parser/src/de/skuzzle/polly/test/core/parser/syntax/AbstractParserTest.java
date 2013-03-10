package de.skuzzle.polly.test.core.parser.syntax;

import org.junit.Ignore;

import de.skuzzle.polly.core.parser.InputParser;
import de.skuzzle.polly.core.parser.problems.SimpleProblemReporter;


/**
 * Base class for all {@link InputParser} tests.
 * 
 * @author Simon Taddiken
 */
@Ignore
public abstract class AbstractParserTest {

    public InputParser obtain(String input) {
        return new InputParser(input, new SimpleProblemReporter());
    }
}