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
package de.skuzzle.polly.http.api.answers;

/**
 * Contains some modifiable default {@link HttpAnswer HttpAnswers} used by the http
 * server implementation.
 * 
 * @author Simon Taddiken
 */
public class DefaultAnswers {

    /**
     * Answer if no {@link de.skuzzle.polly.http.api.HttpEventHandler HttpEventHandler}
     * could handle the request. 
     */
    public static HttpAnswer FILE_NOT_FOUND = 
        HttpAnswers.createStringAnswer("File not found");
    
    /** Answer if blocked session performed a request */
    public static HttpAnswer SESSION_BLOCKED = 
        HttpAnswers.createStringAnswer("Your session is blocked");
    
    
    
    private DefaultAnswers() {
    }
}
