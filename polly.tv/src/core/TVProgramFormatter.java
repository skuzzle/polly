package core;

import de.skuzzle.polly.sdk.FormatManager;


public interface TVProgramFormatter {

    public abstract String format(TVProgram input, FormatManager formatter);
}