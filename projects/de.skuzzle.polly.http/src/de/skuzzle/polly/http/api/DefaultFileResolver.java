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
import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Default {@link FileResolver} implementation.
 * 
 * @author Simon Taddiken
 */
public class DefaultFileResolver implements FileResolver {
    
    @Override
    public ResolvedFile resolve(String rootDir, String file) 
            throws FileNotFoundException {
        final File inpFile = new File(rootDir, file).getAbsoluteFile();      
        final Path inpPath = inpFile.toPath().normalize();
        
        final File rootFile = new File(rootDir).getAbsoluteFile();
        final Path rootPath = rootFile.toPath().normalize();

        if (inpPath.startsWith(rootPath)) {
            if (!inpFile.exists()) {
                // file does not exist at all
                throw new FileNotFoundException(inpPath.toString());
            }
            // file is relative to given directory 
            return new ResolvedFile() {
                @Override
                public File getFile() {
                    return inpPath.toFile();
                }
            };
        }
        throw new FileNotFoundException("file '" + file + "' in '" + rootDir + "'");
    }
}
