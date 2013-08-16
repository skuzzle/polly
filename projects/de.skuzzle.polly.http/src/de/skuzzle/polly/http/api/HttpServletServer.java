package de.skuzzle.polly.http.api;



public interface HttpServletServer extends HttpServer {

    public void registerParameterHandler(ParameterHandler handler);
    
    public void addController(Controller controller);
}
