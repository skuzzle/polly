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
package de.skuzzle.polly.http.internal;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.answers.HttpTemplateAnswer;


class TemplateAnswerHandler implements HttpAnswerHandler {

    @Override
    public void handleAnswer(HttpAnswer answer, HttpEvent e, OutputStream out) 
            throws IOException {
        final HttpTemplateAnswer template = (HttpTemplateAnswer) answer;
        
        // try to resolve template file and init velocity
        final File templateFile = e.getSource().resolveRelativeFile(
            template.getRelativeTemplatePath());
        
        final VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, 
            templateFile.getParent());
        ve.init();
        final Template temp = ve.getTemplate(templateFile.getName());
        
        final Map<String, Object> mappings = new HashMap<>();
        template.getAnswer(mappings);
        
        final VelocityContext c = new VelocityContext(mappings);
        final OutputStreamWriter w = new OutputStreamWriter(out);
        temp.merge(c, w);
    }

}
