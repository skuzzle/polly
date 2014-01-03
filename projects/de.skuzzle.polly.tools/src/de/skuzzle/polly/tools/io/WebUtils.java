package de.skuzzle.polly.tools.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;



public final class WebUtils {
    
    
    public final static String DEFAULT_CHARSET = "UTF-8"; 
    
    

    public static String makeUrlQueryPart(Map<String, ? extends Object> parameters) {
        return makeUrlQueryPart(parameters, DEFAULT_CHARSET);
    }
    
    
    
    public static String makeUrlQueryPart(Map<String, ? extends Object> parameters, 
            String targetCharset) {
        final StringBuilder b = new StringBuilder();
        appendParameters(b, "?", parameters, targetCharset);
        return b.toString();
    }
    
    
    
    public static String appendUrlQueryPart(String url, Map<String, ? extends Object> parameters, 
            String targetCharset) {
        final StringBuilder b = new StringBuilder(url);
        final String delimiter = url.indexOf("?") == -1 ? "?" : "&";
        appendParameters(b, delimiter, parameters, targetCharset);
        return b.toString();
    }
    
    
    
    private static void appendParameters(StringBuilder b, String initialDelimiter, 
            Map<String, ? extends Object> parameters, String targetCharset) {
        try {
            Charset.forName(targetCharset);
        } catch (Exception e) {
            e.printStackTrace();
            targetCharset = Charset.defaultCharset().name();
        }
        try {
            String delimiter = initialDelimiter;
            for (final Entry<String, ? extends Object> e : parameters.entrySet()) {
                b.append(delimiter);
                delimiter = "&";
                b.append(URLEncoder.encode(e.getKey(), targetCharset));
                b.append("=");
                b.append(URLEncoder.encode(e.getValue().toString(), targetCharset));
            }
        } catch (UnsupportedEncodingException e) {
            // not reachable
            assert false;
        }
    }
    
    
    
    public static void getString(String targetUrl, Map<String, ? extends Object> parameters, String targetCharset, Appendable redirect) throws IOException {
        final String surl = appendUrlQueryPart(targetUrl, parameters, targetCharset);
        final URL url = new URL(surl);
        
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String cs = connection.getContentEncoding();
        if (cs == null) {
            cs = DEFAULT_CHARSET;
        }
        try (final BufferedReader r = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), cs))) {
            String line = null;
            while ((line = r.readLine()) != null) {
                redirect.append(line);
                redirect.append(System.lineSeparator());
            }
        }
    }
    
    
    
    public static StringBuilder getString(String targetUrl, Map<String, ? extends Object> parameters, 
            String targetCharset) throws IOException {
        final StringBuilder b = new StringBuilder();
        getString(targetUrl, parameters, targetCharset, b);
        return b;
    }
    
    
    
    public static StringBuilder getString(String targetUrl) throws IOException {
        final String cs = DEFAULT_CHARSET;
        final Map<String, ? extends Object> m = Collections.emptyMap();
        return getString(targetUrl, m, cs);
    }

    
    
    private WebUtils() {}
}