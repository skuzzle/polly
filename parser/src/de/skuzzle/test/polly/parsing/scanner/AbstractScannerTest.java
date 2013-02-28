package de.skuzzle.test.polly.parsing.scanner;

import de.skuzzle.polly.parsing.AbstractTokenStream;
import de.skuzzle.polly.parsing.InputScanner;


public class AbstractScannerTest {

    /**
     * Creates a new AbstractTokenStream instance for the given input.
     * 
     * @param input Scanner input.
     * @return New token stream.
     */
    protected AbstractTokenStream obtain(String input) {
        return new InputScanner(input);
    }
}
