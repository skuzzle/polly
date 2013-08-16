package de.skuzzle.polly.http.api.answers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpCookie;


public class AbstractHttpAnswer extends HttpAnswer {

    private final int responseCode;
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
    public Collection<HttpCookie> getCookies() {
        return this.cookies;
    }
}
