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
package de.skuzzle.polly.http.api.handler;

import java.io.IOException;
import java.io.OutputStream;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.answers.HttpBinaryAnswer;


/**
 * Default handler for all subclasses of {@link HttpBinaryAnswer}. This handler
 * is automatically registered with the servlet server upon creation.
 * 
 * @author Simon Taddiken
 */
public class BinaryAnswerHandler extends HttpAnswerHandler {

    @Override
    public void handleAnswer(HttpAnswer answer, HttpEvent e, OutputStream out)
            throws IOException {
        final HttpBinaryAnswer binary = (HttpBinaryAnswer) answer;
            binary.getAnswer(out, e.getSource());
    }
}
