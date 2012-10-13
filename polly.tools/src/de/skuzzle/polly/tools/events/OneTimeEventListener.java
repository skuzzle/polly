package de.skuzzle.polly.tools.events;

import java.util.EventListener;

/**
 * this is kind of a tagging interface for event listener which will only be
 * notified once. After being notified, the listener is removed from the 
 * {@link EventProvider} it was registered at.
 * @author Simon
 */
public interface OneTimeEventListener extends EventListener {}
