package de.skuzzle.polly.http.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.Get;
import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.http.api.Param;
import de.skuzzle.polly.http.api.ParameterHandler;
import de.skuzzle.polly.http.api.Post;
import de.skuzzle.polly.http.api.ServerFactory;
import de.skuzzle.polly.http.api.HttpEvent.RequestMode;
import de.skuzzle.polly.http.api.answers.HttpAnswer;


class HttpServletServerImpl extends HttpServerImpl implements HttpServletServer {

    private final List<ParameterHandler> paramHandler;
    
    public HttpServletServerImpl(ServerFactory factory) {
        super(factory);
        this.paramHandler = new ArrayList<ParameterHandler>();
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

    
    
    private void findRequestHandlers(Controller carrier, Class<?> cls) {
        for (final Method mtd : cls.getMethods()) {
            final RequestMode mode;
            String url;
            if (mtd.isAnnotationPresent(Get.class)) {
                mode = RequestMode.GET;
                url = mtd.getAnnotation(Get.class).value();
            } else if (mtd.isAnnotationPresent(Post.class)) {
                mode = RequestMode.POST;
                url = mtd.getAnnotation(Post.class).value();
            } else {
                continue;
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
}
