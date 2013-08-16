package de.skuzzle.http.sample;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;


import de.skuzzle.polly.http.api.DefaultServerFactory;
import de.skuzzle.polly.http.api.FileHttpEventHandler;
import de.skuzzle.polly.http.api.HttpServletServer;
import de.skuzzle.polly.http.api.ServerFactory;
import de.skuzzle.polly.http.internal.HttpServerCreator;


public class Main {

    public static void main(String[] args) throws IOException {
        final ServerFactory sf = new DefaultServerFactory(80, Executors.newCachedThreadPool());
        
        final HttpServletServer hss = HttpServerCreator.createServletServer(sf);
        hss.addWebRoot(new File("web"));
        
        final MyController mc = new MyController();
        hss.addController(mc);
        
        hss.registerHttpEventHandler(new FileHttpEventHandler("/files", false));
        hss.start();
    }
}
