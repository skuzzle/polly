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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.skuzzle.polly.http.api.answers.HttpAnswer;

/**
 * This annotation is used in conjunction with {@link Servlet http servlets}. Methods
 * can be annotated to define event handlers for actions that the servlet was called with.
 * 
 * <p>Annotated methods must have an exact signature:</p>
 * <ul>
 * <li>They can be static</li>
 * <li>They can have any access modifier</li>
 * <li>They must return a sub type of {@link HttpAnswer}</li>
 * <li>They must have exactly one parameter of type {@link HttpEvent}</li>
 * <li>They may either throw no exception or a {@link HttpException}</li>
 * </ul>
 * <p>If any of the above conditions are not satisfied, an 
 * {@link IllegalArgumentException} will be thrown when trying to add the method as a 
 * handler to a {@link Servlet}.</p>
 * 
 * <p>The single string value of this annotation specifies the name of the action that 
 * the annotated method handles (this is case sensitive!). Here is an example:</p>
 * <pre>
 * &#64;HandlesAction("addUser")
 * public HttpAnswer addUser(HttpEvent e) throws HttpException {
 *     if (!db.addUser) {
 *         return HttpAnswers.createStringAnswer("user could not be added");
 *     } else {
 *         return HttpAnswers.createStringAnswer("user added!");
 *     }
 * }
 * </pre>
 * @author Simon Taddiken
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlesAction {
    /**
     * Defines the name of the action which is handled by the annotated method.
     * 
     * @return The name of the handled action.
     */
    String value();
}