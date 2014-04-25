package de.skuzzle.polly.tools.io;


import de.skuzzle.jeve.Listener;


public interface StrategyChangedListener extends Listener {
    
    public void strategyChanged(StrategyChangedEvent e);
}
