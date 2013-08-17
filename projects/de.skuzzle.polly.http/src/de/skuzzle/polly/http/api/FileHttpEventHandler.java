package de.skuzzle.polly.http.api;

import java.io.FileNotFoundException;

import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;


public class FileHttpEventHandler implements HttpEventHandler {

    private final String uriPrefix;
    private final boolean executeFollowers;
    
    
    
    public FileHttpEventHandler(String uriPrefix, boolean executeFollowers) {
        this.uriPrefix = uriPrefix;
        this.executeFollowers = executeFollowers;
    }
    
    
    
    @Override
    public HttpAnswer handleHttpEvent(HttpEvent e, HttpEventHandler next)
            throws HttpException {
        System.out.println(e.getRequestURI());
        if (!e.getPlainUri().startsWith(this.uriPrefix)) {
            return next.handleHttpEvent(e, next);
        }
        
        HttpAnswer backup = null;
        if (this.executeFollowers) {
            backup = next.handleHttpEvent(e, next);
        }
        
        try {
            return HttpAnswers.createFileAnswer(e.getPlainUri(), e.getSource());
        } catch (FileNotFoundException e1) {
            if (backup != null) {
                return backup;
            }
            throw new HttpException(e1);
        }
    }

}
