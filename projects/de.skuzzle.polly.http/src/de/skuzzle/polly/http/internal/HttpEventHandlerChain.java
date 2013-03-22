package de.skuzzle.polly.http.internal;

import java.util.Iterator;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpEventHandler;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.answers.HttpAnswer;


final class HttpEventHandlerChain implements HttpEventHandler {

    private final Iterator<HttpEventHandler> it;
    
    
    public HttpEventHandlerChain(Iterator<HttpEventHandler> it) {
        this.it = it;
    }
    
    
    
    @Override
    public HttpAnswer handleHttpEvent(HttpEvent e, HttpEventHandler chain) 
            throws HttpException {
        if (this.it.hasNext()) {
            return this.it.next().handleHttpEvent(e, this);
        }
        return null;
    }

}
