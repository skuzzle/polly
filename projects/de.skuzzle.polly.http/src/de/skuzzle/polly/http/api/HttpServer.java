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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.http.api.answers.HttpBinaryAnswer;
import de.skuzzle.polly.http.api.answers.HttpTemplateAnswer;



public interface HttpServer {
    
    public final static int SESSION_TYPE_COOKIE = 0;
    
    public final static int SESSION_TYPE_IP = 1;
    
    public final static int SESSION_TYPE_GET = 2;

    /**
     * Determines whether the server is currently running.
     * 
     * @return Whether the server is currently running.
     */
    public boolean isRunning();
    
    /**
     * Starts the http server on the specified port if it is not already running using 
     * the provided {@link ExecutorService} as threading strategy. If the server is 
     * already running, an {@link IllegalStateException} will be thrown.
     * 
     * @param port The port for the server.
     * @param service ExecutorService used to create thread for event handling.
     * @throws IOException If running the server fails.
     */
    public void start(int port, ExecutorService service) throws IOException;
    
    /**
     * Starts the http server on the specified port if it is not already running using a
     * default threading strategy. If the server is already running, an
     * {@link IllegalStateException} will be thrown.
     * 
     * @param port The port for the server.
     * @throws IOException If running the server fails.
     */
    public void start(int port) throws IOException;
    
    /**
     * Stops the server if it is currently running and clears all active sessions. If the 
     * server is not running, an {@link IllegalStateException} will be thrown.
     * 
     * @param timeout Timeout to wait for active event handlers in seconds.
     */
    public void shutdown(int timeout);
    
    public int getSessionType();
    
    /**
     * Time in milliseconds after which a session should expire.
     * 
     * @return The session expiration time.
     */
    public int sessionLiveTime();
    
    /**
     * Registers the given handler which then might be notified about new http events.
     * The handler is not notified if another handler which was registered earlier 
     * can successfully handle the event. See {@link HttpEventHandler} for details on how
     * handler chaining works.
     * 
     * @param handler The event handler to register.
     */
    public void registerHttpEventHandler(HttpEventHandler handler);
    
    /**
     * Removes the given handler.
     * 
     * @param handler The handler to remove.
     */
    public void unregisterHttpEventHandler(HttpEventHandler handler);
    
    /**
     * <p>Adds the given directory as a root directory. If files are to be provided over
     * http, they must be absolutely relative to any of the root directories that are 
     * registered with this method. This ensures that no files are sent that exist 
     * outside a directory which is intended to contain web files.</p>
     * 
     * @param directory The directory to add as web root.
     * @see #resolveRelativeFile(String)
     */
    public void addWebRoot(File directory);

    /**
     * <p>Resolves a file by its relative path. This searches all registered web root
     * directories for a file with the given relative path. The first so found file 
     * will be returned.</p>
     * 
     * <p>This method takes care that the given path does not reference files outside 
     * a web root directory using '..'.</p>
     * 
     * @param path Path to a file relative to any of the registered root directories.
     * @return The resolved file.
     * @throws FileNotFoundException If the specified file does not exist or could not
     *          be resolved because it's outside a web root directory.
     */
    public File resolveRelativeFile(String path) throws FileNotFoundException;

    /**
     * <p>This method will resolve a suitable {@link HttpAnswerHandler} for the given 
     * answer. If no handler for the concrete type of the answer is found, this method 
     * tries to find a handler for any of the answer's super types. If still none could 
     * be found, <code>null</code> is returned.</p>
     * 
     * <p>Custom answer handler can be provided using <code>registerHandler()</code>
     * method. By default, handlers for {@link HttpTemplateAnswer} and 
     * {@link HttpBinaryAnswer} exist.</p>
     *  
     * @param answer The answer for which a handler should be resolved.
     * @return The resolved handler or <code>null</code> if none was found.
     * @see #registerHandler(Class, HttpAnswerHandler)
     */
    public HttpAnswerHandler getHandler(HttpAnswer answer);
    
    /**
     * Registers a {@link HttpAnswerHandler answer handler} as default handler for 
     * answers of the given class's type. The given handler will also be used for
     * all sub types of the specified class if no more concrete handler is registered.
     *  
     * @param answerType Type of the answer to handle. This should be a sub class of
     *          {@link HttpAnswer}.
     * @param handler The handler which can handle answers of the given type.
     */
    public void registerHandler(Class<?> answerType, HttpAnswerHandler handler);
}
