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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.answers.HttpTemplateAnswer;

/**
 * Default handler for all subclasses of {@link HttpTemplateAnswer}. This handler
 * is automatically registered with the servlet server upon creation.
 * 
 * @author Simon Taddiken
 */
public class TemplateAnswerHandler extends HttpAnswerHandler {

    @Override
    public void handleAnswer(HttpAnswer answer, HttpEvent e, OutputStream out) 
            throws IOException {
        
        try {
            final HttpTemplateAnswer template = (HttpTemplateAnswer) answer;
            
            // try to resolve template file and init velocity
            final VelocityEngine ve = new VelocityEngine();
            this.prepare(ve, template.getName());
            
            ve.init();
            final Template temp = ve.getTemplate(template.getName());
            
            final Map<String, Object> mappings = new HashMap<>();
            template.getAnswer(mappings);
            
            final VelocityContext c = new VelocityContext(mappings);
            final Writer w = new BufferedWriter(new OutputStreamWriter(out));
            temp.merge(c, w);
            w.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
            throw e1;
        }
    }
    
    
    
    /**
     * Prepares the velocity engine to render the requested template.
     * 
     * @param ve The velocity engine.
     * @param templatePath The requested template path
     */
    protected void prepare(VelocityEngine ve, String templateFile) {
        ve.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
        ve.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
        ve.setProperty("file.resource.loader.path", 
            new File(templateFile).getParent().toString());
    }
}
