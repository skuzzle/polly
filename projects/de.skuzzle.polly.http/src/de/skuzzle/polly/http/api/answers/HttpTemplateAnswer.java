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

import java.util.Map;

import com.sun.net.httpserver.HttpHandler;

import de.skuzzle.polly.http.api.HttpServer;

/**
 * <p>This type of answer can be used to render velocity templates and send the result 
 * back to the client. All that needs to be done is to specify a path to the template
 * file and to provide a context map with data for that template. Instances of this type 
 * can automatically be handled by the server, so that there is no need to provide your 
 * own {@link HttpHandler} if you implement this interface (unless you need special 
 * treatment for your implementation).</p>
 * 
 * <p>The path to the template file must be relative to any of the 
 * {@link HttpServer HttpServer's} web roots in order for this answer to being 
 * successfully handled.</p>
 * 
 * <p>You can obtain concrete instances of this interface using {@link HttpAnswers}.</p>
 * 
 * @author Simon Taddiken
 */
public interface HttpTemplateAnswer extends HttpAnswer {

    /**
     * Returns a relative path to the velocity template file that should be rendered. The 
     * returned path must be relative to any of the {@link HttpServer HttpServer's} web 
     * root directories.
     * 
     * @return relative template file path.
     */
    public String getRelativeTemplatePath();
    
    /**
     * This method must fill the provided map with own velocity context mappings for the 
     * template that is provided by {@link #getRelativeTemplatePath()}.
     * 
     * @param mappings Result map for velocity context.
     */
    public void getAnswer(Map<String, Object> mappings);
}