package de.skuzzle.polly.http.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpEvent.RequestMode;
import de.skuzzle.polly.http.api.HttpEventHandler;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.Param;
import de.skuzzle.polly.http.api.ParameterHandler;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;


class ReflectionHttpHandler implements HttpEventHandler {
    
    private final String uri;
    private final Method handler;
    private final RequestMode mode;
    private final Controller carrier;
    private final HttpServletServerImpl parent;
    
    
    public ReflectionHttpHandler(RequestMode mode, String uri, Controller carrier, 
            Method handler, HttpServletServerImpl parent) {
        this.mode = mode;
        this.carrier = carrier;
        this.uri = uri;
        this.handler = handler;
        this.parent = parent;
    }
    
    
    
    @Override
    public HttpAnswer handleHttpEvent(HttpEvent e, HttpEventHandler next)
            throws HttpException {
        
        if (e.getMode() != this.mode || 
                !e.getPlainUri().equals(this.uri)) {
            return next.handleHttpEvent(e, next);
        }
        
        
        // extract actual parameters from the request
        final Object[] params = new Object[this.handler.getParameterTypes().length];
        for (int i = 0; i < this.handler.getParameterTypes().length; ++i) {
            final Annotation[] an = this.handler.getParameterAnnotations()[i];
            
            // extract parameter name from annotated method parameter
            // INVARIANT: every parameter is annotated!
            Param key = null;
            for (Annotation a : an) {
                if (a instanceof Param) {
                    key = (Param) a;
                    break;
                }
            }
            
            // value associated with that key in the current request 
            final String sValue = e.parameterMap(this.mode).get(key.value());
            if (sValue == null) {
                return HttpAnswers.createStringAnswer("missing parameter: " + 
                    key.value());
            }
            
            final Class<?> type = this.handler.getParameterTypes()[i];
            final ParameterHandler ph = this.parent.findHandler(type, key.typeHint());
            // INVARIANT: ph can not be null
            assert ph != null;
            
            params[i] = ph.parse(sValue);
        }
        
        // execute the function
        try {
            final Controller copy = this.carrier.bind(e);
            return (HttpAnswer) this.handler.invoke(copy, params);
        } catch (InvocationTargetException e1) {
            throw new HttpException(e1.getTargetException());
        } catch (Exception e1) {
            throw new HttpException(e1);
        }
    }
}
