package de.skuzzle.polly.tools.streams;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;


public interface StrategyChangedListener extends EventListener {
    
    public final static Dispatch<StrategyChangedListener, StrategyChangedEvent> STRATEY_CHANGED = 
            new Dispatch<StrategyChangedListener, StrategyChangedEvent>() {

        @Override
        public void dispatch(StrategyChangedListener listener, StrategyChangedEvent event) {
            listener.strategyChanged(event);
        }
    };

    
    
    public void strategyChanged(StrategyChangedEvent e);
}
