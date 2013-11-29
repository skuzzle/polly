package de.skuzzle.polly.tools.events;

import java.util.EventListener;


public interface Dispatch<L extends EventListener, E extends Event<?>> {

    public void dispatch(L listener, E event);
}
