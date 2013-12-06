package de.skuzzle.polly.core.eventhandler;

import java.util.regex.Pattern;

import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageType;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;


public class FreenodeAutoLoginProvider implements AutoLoginProvider {
    
    @Override
    public boolean supportsNetwork(String server) {
        return server.toLowerCase().contains("freenode"); //$NON-NLS-1$
    }

    @Override
    public void requestAuthentification(String forUser, IrcManager irc) {
        irc.sendRawCommand("NICKSERV INFO " + forUser); //$NON-NLS-1$
    }

    @Override
    public boolean processMessageEvent(MessageEvent e, UserManagerImpl users)
            throws AlreadySignedOnException, UnknownUserException {
        if (e.getType() != MessageType.NOTICE) {
            return false;
        } else if (!e.getUser().getNickName().toLowerCase().equals("nickserv")) { //$NON-NLS-1$
            return false;
        }
        
        //[12:52:53] -NickServ- Information on simon_t (account uschi):
        final int i = e.getMessage().indexOf("Information on "); //$NON-NLS-1$
        if (i != -1) {
            final int j = e.getMessage().indexOf(" ", i + 1); //$NON-NLS-1$
            final String forUser = e.getMessage().substring(i, j);
            users.logonWithoutPassword(forUser);
            return true;
        }
        return false;
    }
}
