package de.skuzzle.polly.http.internal;

import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.http.api.ServerFactory;

/**
 * Class to actually obtain instances of {@link HttpServer} and {@link HttpServletServer}.
 * 
 * @author Simon Taddiken
 */
public final class HttpServerCreator {

    public static HttpServer createServer(ServerFactory sf) {
        return new HttpServerImpl(sf);
    }
    
    
    public static HttpServletServer createServletServer(ServerFactory sf) {
        return new HttpServletServerImpl(sf);
    }
    
    
    private HttpServerCreator() {}
}
