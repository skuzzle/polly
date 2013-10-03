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

import de.skuzzle.polly.http.api.answers.HttpAnswer;

/**
 * This exception can be thrown within any {@link HttpEventHandler}. It will then stop
 * the delegation of the current event chain and the server will sent the 
 * {@link HttpAnswer} carried by this exception.
 * @author Simon Taddiken
 */
public class AlternativeAnswerException extends HttpException {

    private static final long serialVersionUID = 1L;

    private final HttpAnswer answer;
    
    
    public AlternativeAnswerException(HttpAnswer answer) {
        this.answer = answer;
    }
    
    
    
    
    public HttpAnswer getAnswer() {
        return this.answer;
    }
}
