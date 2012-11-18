package de.skuzzle.polly.sdk;

import java.io.PrintWriter;

/**
 * Dataclasses can implement this interface to provide csv serializing.
 * 
 * @author Simon Taddiken
 * @since 0.9.1
 */
public interface CSVExportable {
    
    
    /**
     * Prints the header of the csv File (column names) to the given {@link PrintWriter}.
     * 
     * @param writer The PrintWriter to write to.
     * @param separator The separator string between each value.
     */
    public abstract void printBanner(PrintWriter writer, String separator);
    
    

    /**
     * Prints the contents of the implementing class as comma separated values to the
     * given {@link PrintWriter}. This method must also print a linebreak at the end.
     * 
     * @param writer The PrintWriter to write to.
     * @param separator The separator string between each value.
     */
    public abstract void printCSVLine(PrintWriter writer, String separator);
}