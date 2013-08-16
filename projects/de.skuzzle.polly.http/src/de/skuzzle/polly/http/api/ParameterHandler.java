package de.skuzzle.polly.http.api;



public interface ParameterHandler {
    public abstract Object parse(String value) throws HttpException;
    
    public abstract boolean canHandle(Class<?> type, Class<?> typeVar);
}
