package de.skuzzle.polly.http.api.answers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpCookie;


public class AbstractHttpAnswer extends HttpAnswer {

    private int responseCode;
    private final Map<String, List<String>> headers;
    private final List<HttpCookie> cookies;
    
    
    public AbstractHttpAnswer(int responseCode) {
        this.responseCode = responseCode;
        this.headers = new HashMap<String, List<String>>();
        this.cookies = new ArrayList<HttpCookie>();
    }
    
    
    
    @Override
    public int getResponseCode() {
        return this.responseCode;
    }

    
    
    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return this.headers;
    }

    
    
    @Override
    public HttpAnswer redirect(String url) {
        this.responseCode = 303;
        this.addHeader("Location", url);
        return this;
    }
    
    
    
    @Override
    public HttpAnswer addHeader(String name, String value) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            this.headers.put(name, values);
        }
        values.add(value);
        return this;
    }
    
    
    
    @Override
    public Collection<HttpCookie> getCookies() {
        return this.cookies;
    }
    
    
    
    @Override
    public HttpAnswer addCookie(HttpCookie cookie) {
        this.cookies.add(cookie);
        return this;
    }
    
    
    
    @Override
    public HttpAnswer addCookie(String name, String value, int maxAge) {
        return this.addCookie(new HttpCookie(name, value, maxAge));
    }
}
