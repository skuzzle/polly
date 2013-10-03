package de.skuzzle.polly.sdk.httpv2.html;

import java.util.List;

import de.skuzzle.polly.http.api.HttpEvent;


public interface HTMLModelListener<T> {

    public void onDataProcessed(HTMLTableModel<T> source, List<T> data, HttpEvent e);
}
