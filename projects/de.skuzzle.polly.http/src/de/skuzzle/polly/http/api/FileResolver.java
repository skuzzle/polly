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

import java.io.FileNotFoundException;

/**
 * This class may be used to resolve {@link File files} relative to a certain directory,
 * ensuring that they are <em>really</em> contained deeper in the directory structure. 
 * This resolves any ambiguities introduced by the path parts <tt>.</tt> and <tt>..</tt>.
 * 
 * @author Simon Taddiken
 */
public interface FileResolver {

    /**
     * Checks whether the file <tt>relative</tt> is contained within any sub folder of
     * <tt>rootDir</tt>. Paths will be normalized in order to eliminate ambiguities 
     * introduced by <tt>.</tt> and <tt>..</tt>. If the resolution was successful, the
     * resulting file will be normalized as well.
     * 
     * <p>Using this method ensures that no files outside the provided root directory are
     * accessed.</p>
     * 
     * @param rootDir Root directory in which to search the provided file
     * @param relative Path to a file to resolve in the root directory.
     * @return The resolved file.
     * @throws FileNotFoundException If the file does not exist at all or is not relative
     *          to the provided root directory.
     */
    public ResolvedFile resolve(String rootDir, String relative) 
            throws FileNotFoundException;
}