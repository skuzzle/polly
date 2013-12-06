package de.skuzzle.polly.core.eventhandler;

import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;


public interface AutoLoginProvider {

    public boolean supportsNetwork(String server);
    
    public void requestAuthentification(String forUser, IrcManager irc);
    
    public boolean processMessageEvent(MessageEvent e, UserManagerImpl users) 
        throws AlreadySignedOnException, UnknownUserException;
}
