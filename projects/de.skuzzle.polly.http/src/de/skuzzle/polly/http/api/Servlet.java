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
package de.skuzzle.polly.http.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.http.api.answers.HttpAnswer;

/**
 * A servlet is a special {@link HttpEventHandler} which provides an easy way to specify
 * parameterized http actions. First, the action which should be executed is determined
 * by a {@link HttpEvent} and a so called 'actionKey'. The action key is the name of
 * a parameter in the event's GET or POST map (depending on this servlet's 
 * {@link #getMode() mode}). The associated value with that key then specifies the actual
 * action which is executed.
 * 
 * <p>Actions are specified in a simple but powerful way using the annotation 
 * {@link HandlesAction}. In your servlet subclass just specify a method with this 
 * annotation and it automatically gets called when a HttpEvent with the specified 
 * action occurs.</p>
 * 
 * <p>All action handlers of your servlet are automatically added. You can add 
 * additional handlers using the {@link #addActionHandlers(Object)} method which will
 * add all action handlers that are contained in the specified object.</p>
 * 
 * <p>You may filter out {@link HttpEvent HttpEvents} that should not be handled by your
 * servlet by overriding {@link #accept(HttpEvent)}. By default, all events are 
 * accepted.</p>
 * 
 * <p>Consider a servlet with mode GET and action key "action":</p>
 * <pre>
 * public class MyServlet extends Servlet {
 * 
 *     private Map&lt;String, String&gt; entries = new HashMap&lt;&gt;();
 * 
 *     public MyServlet() {
 *        super(Mode.GET, "action");
 *     }
 *     
 *     &#64;HandlesAction("delEntry")
 *     private HttpAnswer deleteEntry(HttpEvent e) {
 *         if (e.get("name") == null) {
 *             return HttpAnswers.createStringAnswer("'name' parameter missing.);
 *         }
 *         this.entries.remove(e.get("name"));
 *         return HttpAnswers.createStringAnswer("'name' has been removed);
 *     }
 *     
 *     &#64;HandlesAction("addEntry")
 *     private HttpAnser addEntry(HttpEvent e) {
 *         if (e.get("name") == null || e.get("value") == null) {
 *             return HttpAnswers.createStringAnswer("required parameter missing");
 *         }
 *         this.entries.put(e.get("name"), e.get("value"));
 *         return HttpAnswers.createStringAnswer("entry has been added");
 *     }
 *     
 *     // ...
 * }
 * </pre>
 * 
 * <p>Now, if a HttpEvent with the following request URI occurs, the addEntry() method 
 * would be called: <code>/bla?action=addEntry&amp;name=foo&amp;value=bar</code><br/>
 * Of course you should also validate whether all required parameters a provided in the
 * http event before using them.</p> 
 * 
 * @author Simon Taddiken
 * @see HandlesAction
 */
public abstract class Servlet implements HttpEventHandler {
    
    private final class ReflectionServletAction {
        private final Method handler;
        private final Object carrier;
        
        
        public ReflectionServletAction(Object carrier, Method handler) {
            this.handler = handler;
            this.carrier = carrier;
        }
        
        
        
        private final HttpAnswer execute(HttpEvent e) throws HttpException {
            try {
                final HttpAnswer a = (HttpAnswer) this.handler.invoke(this.carrier, e);
                return a;
            } catch (InvocationTargetException e1) {
                if (e1.getTargetException() instanceof HttpException) {
                    final HttpException e2 = (HttpException) e1.getTargetException();
                    throw e2;
                } else {
                    final RuntimeException e2 = 
                        (RuntimeException) e1.getTargetException();
                    throw e2;
                }
            } catch (IllegalAccessException | IllegalArgumentException e1) {
                e1.printStackTrace();
                throw new HttpException(e1);
            }
        }
    }

    
    
    /**
     * Specifies which of a HttpEvent's parameters should be used to identify
     * an action.
     * 
     * @author Simon Taddiken
     */
    public enum Mode {
        /** Use POST parameters*/
        POST, 
        /** Use GET parameters */
        GET, 
        /** Use POST and GET parameters*/
        BOTH;
    }
    
    
    
    /** Current parameter retrieving mode */
    protected final Mode mode;
    
    /** Name of the key in the parameter map which identifies the action */
    protected final String actionKey;
    
    private final Map<String, ReflectionServletAction> actionMap;
    
    
    
    /**
     * Creates a new Servlet. The specified <code>mode</code> determines which of the
     * HttpEvent's parameters should be used to identify an action. The 
     * <code>actionKey</code> is then used to identify the name of the action in the
     * event's corresponding parameter map.
     * 
     * <p>Additionally, all methods annotated with {@link HandlesAction} contained in 
     * this subclass will be added to this servlet's handler map.</p> 
     *  
     * @param mode Which parameters should be used to determine the action.
     * @param actionKey Name of a key in the HttpEvent's parameters which references the
     *          action to execute.
     */
    public Servlet(Mode mode, String actionKey) {
        this.mode = mode;
        this.actionKey = actionKey;
        this.actionMap = new HashMap<>();
        this.addActionHandlers(this);
    }
    
    
    
    /**
     * Determines which parameters of a HttpEvent are used to determine the name of the
     * action to execute.
     * 
     * @return The mode.
     */
    public Mode getMode() {
        return this.mode;
    }
    
    
    
    /**
     * Adds all static methods which are annotated with {@link HandlesAction} from the 
     * given class to this servlet's handler map. This method throws 
     * {@link IllegalArgumentException IllegalArgumentExceptions} if any of the annotated 
     * methods do not satisfy the {@link HandlesAction} signature contract or a handler 
     * for the same name is already registered.
     * 
     * @param carrier Type which static methods should be searched for action handlers.
     */
    public void addStaticHandlers(Class<?> carrier) {
        this.addActionHandlers(null, carrier);
    }
    
    
    
    /**
     * Adds all static and non-static methods which are annotated with 
     * {@link HandlesAction} from the given object's type to this servlet's handler map.
     * This method throws {@link IllegalArgumentException IllegalArgumentExceptions} if 
     * any of the annotated methods do not satisfy the {@link HandlesAction} 
     * signature contract or a handler for the same name is already registered.
     * 
     * @param carrier The object from which action handlers should be registered.
     */
    public void addActionHandlers(Object carrier) {
        this.addActionHandlers(carrier, carrier.getClass());
    }
    
    
    
    private final void addActionHandlers(Object carrier, Class<?> cls) {
        for (final Method mtd : cls.getMethods()) {
            if (mtd.isAnnotationPresent(HandlesAction.class)) {
                
                // check if handler for that action name already exists
                final String actionName = mtd.getAnnotation(HandlesAction.class).value();
                if (this.actionMap.containsKey(actionName)) {
                    throw new IllegalArgumentException("handler with name " + 
                        actionName + " already present");
                }
                
                
                // check signature
                if (!HttpAnswer.class.isAssignableFrom(mtd.getReturnType())) {
                    throw new IllegalArgumentException("handler has illegal return type");
                }
                
                if (!(mtd.getParameterTypes().length == 1 && 
                    HttpEvent.class.isAssignableFrom(mtd.getParameterTypes()[0]))) {
                    
                    throw new IllegalArgumentException("handler has illegal signature");
                }
                final Class<?>[] ex = mtd.getExceptionTypes();
                if (ex.length > 1 || 
                    (ex.length == 1 && !HttpException.class.isAssignableFrom(ex[0]))) {
                    
                    throw new IllegalArgumentException(
                        "handler throws illegal exception");
                }
                
                mtd.setAccessible(true);
                boolean isStatic = Modifier.isStatic(mtd.getModifiers());
                this.actionMap.put(actionName, 
                    new ReflectionServletAction(isStatic ? null : carrier, mtd));
            }
        }
    }
    
    
    
    /**
     * Determines whether the given {@link HttpEvent} should be handled by this servlet.
     * If it returns <code>false</code>, the event will be reached to the next 
     * {@link HttpEventHandler event handler} in the chain. If <code>true</code>, the
     * event will be handled by this event, trying to execute any of the registered
     * servlet actions.
     * 
     * <p>By default this method returns <code>true</code> for every event.</p>
     * 
     * @param e The HttpEvent to test.
     * @return Whether ther specified even should be handled by this servlet.
     */
    protected boolean accept(HttpEvent e) {
        return true;
    }
    
    
    
    @Override
    public final HttpAnswer handleHttpEvent(HttpEvent e, HttpEventHandler next)
            throws HttpException {
        
        if (!this.accept(e)) {
            return next.handleHttpEvent(e, next);
        }
        
        final Map<String, String> params;
        switch (this.mode) {
        default:
        case GET:
            params = e.postMap();
            break;
        case POST:
            params = e.getMap();
            break;
        case BOTH:
            params = e.postGetMap();
            break;
        }
        
        final String actionName = params.get(this.actionKey);
        if (actionName == null) {
            return next.handleHttpEvent(e, next);
        }
        
        final ReflectionServletAction sa = this.actionMap.get(actionName);
        if (sa == null) {
            return this.noActionAnswer(e, actionName, next);
        }
        
        try {
            final HttpAnswer answer = sa.execute(e);
            if (answer == null) {
                throw new NullPointerException("ServletAction answer may not be null");
            }
            return answer;
        } catch (HttpException e1) {
            return this.exceptionAnswer(e, e1, next);
        }
    }
    
    
    
    /**
     * <p>This method is called when a {@link HttpEvent} was accepted to be executed by
     * this servlet, but no action is specified for the value of this servlet's
     * action key.</p>
     * 
     * <p>Using this method you can then provide a proper HttpAnswer for that exceptional
     * case.</p>
     * 
     * @param event The HttpEvent.
     * @param actionName Name of the action that was requested but has no handler 
     *          registered.
     * @param next HttpEventHandler chain. Call this object's handleEvent() method
     *          to reach execution of the event to the next handler in the chain.
     * @return The HttpAnswer as response to the provided event.
     * @throws HttpException If further handling of the event should be aborted.
     */
    protected abstract HttpAnswer noActionAnswer(HttpEvent event, 
        String actionName, HttpEventHandler next) throws HttpException;

    
    
    /**
     * This method is called when an event handler for an action throws a HttpException 
     * when being executed. Using this method you can then provide a proper HttpAnswer 
     * for those exceptional cases.</p>
     * 
     * @param event The HttpEvent.
     * @param e The exception that occurred during execution of the action.
     * @param next HttpEventHandler chain. Call this object's handleEvent() method
     *          to reach execution of the event to the next handler in the chain.
     * @return The HttpAnswer as response to the provided event.
     * @throws HttpException If further handling of the event should be aborted.
     */
    protected abstract HttpAnswer exceptionAnswer(HttpEvent event, 
        HttpException e, HttpEventHandler next) throws HttpException;
}
