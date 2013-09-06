package de.skuzzle.polly.http.api;

import java.util.Map;



public interface HttpServletServer extends HttpServer {

    public void addParameterHandler(ParameterHandler handler);
    
    public void addController(Controller controller);
    
    public void addAddHandlerListener(AddHandlerListener listener);
    
    public void removeAddHandlerListener(AddHandlerListener listener);
    
    public Map<String, String> getHandledUrls();
}
