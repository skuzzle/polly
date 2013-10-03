package de.skuzzle.polly.sdk.httpv2.html;

import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.roles.SecurityObject;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;


public interface HTMLTableModel<T> extends SecurityObject {

    public boolean isFilterable(int column);
    
    public boolean isSortable(int column);
    
    public boolean isEditable(int column);
    
    public SuccessResult setCellValue(int column, T element, String value, User executor, 
        MyPolly myPolly);
    
    public String getHeader(int column);
    
    public int getColumnCount();
    
    public Object getCellValue(int column, T element);
    
    public Map<String, String> getRequestParameters(HttpEvent e);
    
    public List<T> getData(HttpEvent e);
    
    public Class<?> getColumnClass(int column);
    
    public boolean isFilterOnly();
    
    public SortOrder getDefaultSortOrder();
    
    public int getDefaultSortColumn();
}