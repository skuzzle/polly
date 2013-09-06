package de.skuzzle.polly.http.api;


public interface AddHandlerListener {

    public void handlerAdded(Controller c, String url, String name, String[] values);
}
