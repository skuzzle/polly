package de.skuzzle.test.polly.parsing;

import de.skuzzle.polly.parsing.InputParser;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.Root;


public class InputParserTest {

    protected void parseCheckAndRun(String s) throws ParseException {
        Namespace context = new Namespace("me");
        Root r = new InputParser().parse(s);
    }
    
    
}
