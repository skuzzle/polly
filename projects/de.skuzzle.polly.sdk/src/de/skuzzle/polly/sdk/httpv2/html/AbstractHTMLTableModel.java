package de.skuzzle.polly.sdk.httpv2.html;

import de.skuzzle.polly.sdk.httpv2.SuccessResult;


public abstract class AbstractHTMLTableModel<T> implements HTMLTableModel<T> {

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
    public SuccessResult setCellValue(int column, int row, String value) {
        return new SuccessResult(false, "Not implemented");
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return Object.class;
    }
}
