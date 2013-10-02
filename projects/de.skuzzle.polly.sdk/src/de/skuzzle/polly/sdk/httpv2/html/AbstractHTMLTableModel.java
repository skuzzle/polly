package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;

public abstract class AbstractHTMLTableModel<T> implements HTMLTableModel<T> {

    private final Set<String> permissions;
    

    
    public AbstractHTMLTableModel() {
        this.permissions = new HashSet<>();
    }

    
    
    protected void requirePermission(String permission) {
        this.permissions.add(permission);
    }
    
    
    
    @Override
    public Set<String> getRequiredPermission() {
        return this.permissions;
    }



    @Override
    public boolean isFilterable(int column) {
        return false;
    }



    @Override
    public boolean isSortable(int column) {
        return false;
    }



    @Override
    public boolean isEditable(int column) {
        return false;
    }

    
    
    @Override
    public boolean isFilterOnly() {
        return false;
    }

    

    @Override
    public SuccessResult setCellValue(int column, T element, String value, 
            User executor, MyPolly myPolly) {
        return new SuccessResult(false, "Not implemented");
    }



    @Override
    public Class<?> getColumnClass(int column) {
        return Object.class;
    }



    @Override
    public Map<String, String> getRequestParameters(HttpEvent e) {
        return Collections.emptyMap();
    }
    
    
    
    @Override
    public int getDefaultSortColumn() {
        return -1;
    }
    
    
    
    @Override
    public SortOrder getDefaultSortOrder() {
        return SortOrder.UNDEFINED;
    }
}
