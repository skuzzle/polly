/*
 * Copyright 2013 Simon Taddiken
 * 
 * This file is part of Polly HTTP API.
 * 
 * Polly HTTP API is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Polly HTTP API is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Polly HTTP API. If not, see http://www.gnu.org/licenses/.
 */
package de.skuzzle.polly.http.api.handler;

import java.io.FileNotFoundException;

import de.skuzzle.polly.http.api.DefaultFileResolver;
import de.skuzzle.polly.http.api.FileResolver;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.ResolvedFile;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpFileAnswer;

public class DirectoryEventHandler extends AbstractFileEventHandler {

    /** Directory in which to search for requested files */
    private final String rootDirectory;



    public DirectoryEventHandler(String rootDirectory, boolean executeFollowers) {
        super(200, executeFollowers);
        this.rootDirectory = rootDirectory;
    }



    @Override
    protected HttpAnswer handleHttpEvent(String registered, HttpEvent e) 
            throws FileNotFoundException, HttpException {

        final String url = e.getPlainUri().substring(registered.length());
        final FileResolver fr = new DefaultFileResolver();
        final ResolvedFile target = fr.resolve(this.rootDirectory, url);

        final HttpAnswer answer = new HttpFileAnswer(this.responseCode, target);
        return answer;
    }
}
