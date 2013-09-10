package de.skuzzle.polly.sdk.httpv2;

import java.util.List;

import de.skuzzle.polly.sdk.util.ColumnFilter;


public interface DataSource<T> {

    public int totalCount();
    
    public List<T> elements(ColumnFilter<T> filter, String[] filterStrings);
    
    public List<T> all();
}
