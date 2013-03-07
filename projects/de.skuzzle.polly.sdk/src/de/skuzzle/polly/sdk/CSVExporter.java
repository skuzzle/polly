package de.skuzzle.polly.sdk;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * This class provides methods to create CSV files from classes that implement
 * {@link CSVExportable}.
 * 
 * @author Simon Taddiken
 * @since 0.9.1
 */
public final class CSVExporter {

    /**
     * Exports a collection of {@link CSVExportable} entities to a csv file with
     * given filename. The single values will be separated by the given separator
     * string.
     * 
     * @param entities The collection of entities to export.
     * @param fileName The path of the resulting csv file.
     * @param separator Separator string for each value.
     * @return <code>false</code>, if the collection was empty which means that no file
     *          has been written. <code>true</code> otherwise.
     * @throws IOException If an I/O error occurred.
     */
    public final static <T extends CSVExportable> boolean exportToCSV(
            Collection<T> entities, String fileName, 
            String separator) throws IOException {
        
        if (entities.isEmpty()) {
            return false;
        }
        
        final CSVExportable first = entities.iterator().next();
        PrintWriter w = null;
        try {
            w = new PrintWriter(fileName);
            
            first.printBanner(w, separator);
            for (final CSVExportable entity : entities) {
                entity.printCSVLine(w, separator);
            }
            
            return true;
        } finally {
            if (w != null) {
                w.close();
            }
        }
    }
}