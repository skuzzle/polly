package de.skuzzle.polly.core.internal.httpv2;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;

import de.skuzzle.polly.http.api.DefaultServerFactory;

public class SSLServerFactory extends DefaultServerFactory {

    private final String keyStore;
    private final String keyStorePw;
    private final String keyPw;
    
    
    
    public SSLServerFactory(int port, ExecutorService executor, String keyStore, 
            String keyStorePw, String keyPw) {
        super(port, executor);
        this.keyStore = keyStore;
        this.keyStorePw = keyStorePw;
        this.keyPw = keyPw;
    }



    public SSLServerFactory(int port, String keyStore, String keyStorePw, String keyPw) {
        super(port);
        this.keyStore = keyStore;
        this.keyStorePw = keyStorePw;
        this.keyPw = keyPw;
    }



    @Override
    public com.sun.net.httpserver.HttpServer create() throws IOException {
        SSLContext context;
        KeyManagerFactory kmf;
        KeyStore ks;
        try {
            context = SSLContext.getInstance("SSLv3"); //$NON-NLS-1$
            kmf = KeyManagerFactory.getInstance("SunX509"); //$NON-NLS-1$
            ks = KeyStore.getInstance("JKS"); //$NON-NLS-1$
            ks.load(new FileInputStream(this.keyStore), this.keyStorePw.toCharArray());
            kmf.init(ks, this.keyPw.toCharArray());
            context.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new IOException(e);
        }
        
        final HttpsServer server = HttpsServer.create(
                new InetSocketAddress(this.port), 5);
        
        final HttpsConfigurator configurator = new HttpsConfigurator(context) {
            @Override
            public void configure(HttpsParameters params) {
                final SSLContext context = this.getSSLContext();
                params.setSSLParameters(context.getDefaultSSLParameters());
            }
        };
        server.setHttpsConfigurator(configurator);
        server.setExecutor(this.executor);
        return server;
    }
}
