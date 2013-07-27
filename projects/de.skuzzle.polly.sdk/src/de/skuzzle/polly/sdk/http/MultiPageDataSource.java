package de.skuzzle.polly.sdk.http;

import java.util.List;


public interface MultiPageDataSource<T> {

    public int getDataCount();
    
    public List<T> getSubData(int first, int limit);
}