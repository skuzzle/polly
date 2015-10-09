package polly.rx.captcha;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Anonymizer {

    private final static int DEFAULT_TIMEOUT = 5000; // milliseconds
    private final static List<Proxy> PROXYS;
    private final static Random RANDOM = new Random();
    private static boolean anonymize = false;
    
    static {
        PROXYS = new ArrayList<>();
        addProxy("cart.chickenkiller.com", 8888); //$NON-NLS-1$
        /*addProxy("212.144.254.122", 3128); //$NON-NLS-1$
        addProxy("188.138.115.15", 3128); //$NON-NLS-1$
        addProxy("46.165.239.133", 3128); //$NON-NLS-1$
        addProxy("62.75.229.121", 3128); //$NON-NLS-1$
        addProxy("87.106.242.46", 80); //$NON-NLS-1$*/
    }
    
    
    
    private static void addProxy(String url, int port) {
        PROXYS.add(new Proxy(Type.HTTP, new InetSocketAddress(url, port)));
    }
    
    
    
    private static Proxy randomProxy() {
        return PROXYS.get(RANDOM.nextInt(PROXYS.size()));
    }
    
    
    
    public static void setAnonymize(boolean anonymize) {
        Anonymizer.anonymize = anonymize;
    }
    
    
    
    public static HttpURLConnection openConnection(URL url) throws IOException {
        if (!anonymize) {
            final HttpURLConnection result = (HttpURLConnection) url.openConnection();
            result.setConnectTimeout(DEFAULT_TIMEOUT);
            return result;
        }
        final Proxy proxy = randomProxy();
        final HttpURLConnection result = (HttpURLConnection) url.openConnection(proxy);
        result.setConnectTimeout(DEFAULT_TIMEOUT);
        return result;
    }
}
