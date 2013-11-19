package de.skuzzle.polly.test.core.parser.scanner;

import org.junit.Ignore;

import de.skuzzle.polly.core.parser.InputScanner;

@Ignore
public abstract class AbstractScannerTest {

    /**
     * Creates a new AbstractTokenStream instance for the given input.
     * 
     * @param input Scanner input.
     * @return New token stream.
     */
    protected InputScanner obtain(String input) {
        return new InputScanner(input);
    }
}
