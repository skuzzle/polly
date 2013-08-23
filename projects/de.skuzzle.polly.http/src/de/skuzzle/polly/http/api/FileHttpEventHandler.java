package de.skuzzle.polly.http.api;

import java.io.FileNotFoundException;

import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;


public class FileHttpEventHandler implements HttpEventHandler {

    private final boolean executeFollowers;
    
    
    
    public FileHttpEventHandler(boolean executeFollowers) {
        this.executeFollowers = executeFollowers;
    }
    
    
    
    @Override
    public HttpAnswer handleHttpEvent(HttpEvent e, HttpEventHandler next)
            throws HttpException {
        System.out.println(e.getRequestURI());
        
        HttpAnswer backup = null;
        if (this.executeFollowers) {
            backup = next.handleHttpEvent(e, next);
        }
        
        try {
            return HttpAnswers.createFileAnswer(e.getPlainUri(), e.getSource())
                .addHeader("Cache-Control", "max-age=86400")      // valid for 24h
                .addHeader("Cache-Control", "must-revalidate");   // obey my rules!
        } catch (FileNotFoundException e1) {
            if (backup != null) {
                return backup;
            }
            throw new HttpException(e1);
        }
    }

}
