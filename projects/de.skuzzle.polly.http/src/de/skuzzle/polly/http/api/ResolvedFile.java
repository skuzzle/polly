/*
 * Copyright 2013 Simon Taddiken
 *
 * This file is part of Polly HTTP API.
 *
 * Polly HTTP API is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 *
 * Polly HTTP API is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with Polly HTTP API. If not, see http://www.gnu.org/licenses/.
 */
package de.skuzzle.polly.http.api;

import java.io.File;
import java.io.IOException;

/**
 * Represents a file which has been resolved to be definitely relative to a certain 
 * directory. For safety reasons, you do not want to make files accessible which are 
 * outside of defined web directories. Using a {@link FileResolver} you can obtain such
 * a checked file.
 *  
 * @author Simon Taddiken
 */
public interface ResolvedFile {

    /**
     * Gets the file which was resolved. This is likely to always be an absolute file. 
     * File resolving can be lazy which is the reason why this method may throw an 
     * IOException.
     * 
     * @return The resolved file.
     * @throws IOException If the file could not be resolved. Throwing this exception is 
     *          optional; implementations should document their behavior.
     */
    public File getFile() throws IOException;
}
