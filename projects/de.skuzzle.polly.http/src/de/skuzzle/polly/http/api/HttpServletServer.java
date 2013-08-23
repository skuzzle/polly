package de.skuzzle.polly.http.api;



public interface HttpServletServer extends HttpServer {

    public void addParameterHandler(ParameterHandler handler);
    
    public void addController(Controller controller);
}
