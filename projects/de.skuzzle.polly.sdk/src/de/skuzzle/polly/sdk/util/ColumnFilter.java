package de.skuzzle.polly.sdk.util;


public interface ColumnFilter<T> {

    public boolean accept(String[] filterString, T element);
}