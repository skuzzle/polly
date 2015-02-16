package de.skuzzle.polly.tools.collections;

import java.util.AbstractList;
import java.util.List;

public class TypeMappingList<T> extends AbstractList<T> {

    private final List<? extends T> back;

    public TypeMappingList(List<? extends T> back) {
        this.back = back;
    }

    @Override
    public T get(int index) {
        return this.back.get(index);
    }


    @Override
    public int size() {
        return this.back.size();
    }

}
