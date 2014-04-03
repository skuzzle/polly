package de.skuzzle.polly.tools.io;

import java.util.EventListener;


public interface StrategyChangedListener extends EventListener {
    
    public void strategyChanged(StrategyChangedEvent e);
}
