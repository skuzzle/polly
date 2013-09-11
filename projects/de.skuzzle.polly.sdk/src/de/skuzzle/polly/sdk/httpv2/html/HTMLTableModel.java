package de.skuzzle.polly.sdk.httpv2.html;

import java.util.List;


public interface HTMLTableModel<T> {

    public boolean isFilterable(int column);
    
    public boolean isSortable(int column);
    
    public String getHeader(int column);
    
    public int getColumnCount();
    
    public Object getCellValue(int column, T element);
    
    public List<T> getData();
}