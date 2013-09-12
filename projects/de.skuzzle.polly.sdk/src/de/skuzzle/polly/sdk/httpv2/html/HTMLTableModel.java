package de.skuzzle.polly.sdk.httpv2.html;

import java.util.List;

import de.skuzzle.polly.sdk.httpv2.SuccessResult;


public interface HTMLTableModel<T> {

    public boolean isFilterable(int column);
    
    public boolean isSortable(int column);
    
    public boolean isEditable(int column);
    
    public SuccessResult setCellValue(int column, String value);
    
    public String getHeader(int column);
    
    public int getColumnCount();
    
    public Object getCellValue(int column, T element);
    
    public List<T> getData();
    
    public Class<?> getColumnClass(int column);
}