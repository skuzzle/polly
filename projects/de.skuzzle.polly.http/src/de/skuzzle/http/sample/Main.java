package de.skuzzle.http.sample;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;


import de.skuzzle.polly.http.api.DefaultServerFactory;
import de.skuzzle.polly.http.api.FileHttpEventHandler;
import de.skuzzle.polly.http.api.HttpServer;
import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.http.api.ServerFactory;
import de.skuzzle.polly.http.internal.HttpServerCreator;


public class Main {

    public static void main(String[] args) throws IOException {
        final ServerFactory sf = new DefaultServerFactory(80, Executors.newCachedThreadPool());
        
        final HttpServletServer hss = HttpServerCreator.createServletServer(sf);
        hss.setSessionType(HttpServer.SESSION_TYPE_COOKIE);
        hss.setSessionLiveTime(60 * 60);
        
        hss.addWebRoot(new File("webv2"));
        
        final MyController mc = new MyController();
        hss.addController(mc);
        
        hss.registerHttpEventHandler(new FileHttpEventHandler("/files", false));
        hss.start();
    }
}
