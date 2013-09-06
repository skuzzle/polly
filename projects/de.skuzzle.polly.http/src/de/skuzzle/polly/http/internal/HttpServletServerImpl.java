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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.HttpEvent.RequestMode;
import de.skuzzle.polly.http.api.AddHandlerListener;
import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.http.api.ParameterHandler;
import de.skuzzle.polly.http.api.ServerFactory;
import de.skuzzle.polly.http.api.answers.HttpAnswer;


class HttpServletServerImpl extends HttpServerImpl implements HttpServletServer {

    private final List<ParameterHandler> paramHandler;
    private final List<AddHandlerListener> addHandlerListener;
    private final Map<String, String> handledUrls;
    
    
    
    public HttpServletServerImpl(ServerFactory factory) {
        super(factory);
        this.paramHandler = new ArrayList<ParameterHandler>();
        this.addHandlerListener = new ArrayList<>();
        this.handledUrls = new HashMap<>();
        this.addParameterHandler(NativeHandlers.INTEGER);
        this.addParameterHandler(NativeHandlers.STRING);
        this.addParameterHandler(NativeHandlers.STRING_LIST);
        this.addParameterHandler(NativeHandlers.INT_LIST);
    }
    
    
    
    @Override
    public void addParameterHandler(ParameterHandler handler) {
        this.paramHandler.add(handler);
    }
    
    

    @Override
    public void addController(Controller carrier) {
        this.findRequestHandlers(carrier, carrier.getClass());
    }

    
    
    @Override
    public void addAddHandlerListener(AddHandlerListener listener) {
        this.addHandlerListener.add(listener);
    }
    
    
    
    @Override
    public void removeAddHandlerListener(AddHandlerListener listener) {
        this.addHandlerListener.remove(listener);
    }
    
    
    
    @Override
    public Map<String, String> getHandledUrls() {
        return this.handledUrls;
    }
    
    
    
    private void findRequestHandlers(Controller carrier, Class<?> cls) {
        for (final Method mtd : cls.getMethods()) {
            final RequestMode mode;
            String url;
            String name;
            if (mtd.isAnnotationPresent(Get.class)) {
                mode = RequestMode.GET;
                url = mtd.getAnnotation(Get.class).value();
                name = mtd.getAnnotation(Get.class).name();
            } else if (mtd.isAnnotationPresent(Post.class)) {
                mode = RequestMode.POST;
                url = mtd.getAnnotation(Post.class).value();
                name = mtd.getAnnotation(Post.class).name();
            } else {
                continue;
            }
            if (name.equals("")) {
                name = mtd.getName();
            }
            
            // validate return type
            if (!HttpAnswer.class.isAssignableFrom(mtd.getReturnType())) {
                throw new IllegalArgumentException("handler has illegal return type");
            }
            
            // validate parameters
            for (int i = 0; i < mtd.getParameterTypes().length; ++i) {
                final Annotation[] an = mtd.getParameterAnnotations()[i];
                
                Param param = null;
                for (Annotation a : an) {
                    if (a instanceof Param) {
                        param = (Param) a;
                        break;
                    }
                }
                
                if (param == null) {
                    throw new IllegalArgumentException(
                        "parameter is not annotated with @Param");
                }
                
                final Class<?> type = mtd.getParameterTypes()[i];
                final Class<?> typeVar = param.typeHint();
                if (this.findHandler(type, typeVar) == null) {
                    throw new IllegalArgumentException("No handler for parameter " 
                        + param.value());
                }
            }
            
            
            if (!url.startsWith("/")) {
                url = "/" + url;
            }
            boolean isStatic = Modifier.isStatic(mtd.getModifiers());
            final ReflectionHttpHandler rhh = new ReflectionHttpHandler(
                mode, url, isStatic ? null : carrier, mtd, this);
            this.addHttpEventHandler(url, rhh);
            
            
            carrier.putHandledURL(this.handledUrls, name, url);
            if (mtd.isAnnotationPresent(OnRegister.class)) {
                final OnRegister te = mtd.getAnnotation(OnRegister.class);
                this.fireHandlerAdded(carrier, url, name, te.value());
            }
        }
        
    }
    
    
    
    ParameterHandler findHandler(Class<?> type, Class<?> typeVar) {
        for (final ParameterHandler ph : this.paramHandler) {
            if (ph.canHandle(type, typeVar)) {
                return ph;
            }
        }
        return null;
    }
    
    
    
    private void fireHandlerAdded(Controller c, String url, String name, String[] values) {
        for (final AddHandlerListener l : this.addHandlerListener) {
            l.handlerAdded(c, url, name, values);
        }
    }
}
