package de.skuzzle.polly.sdk.httpv2.html;


public interface CellEditor {

    public HTMLInput renderEditorCell(Object cellContent, boolean forFilter);
    
    public String getEditIndicator();
}
