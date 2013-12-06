package de.skuzzle.polly.core.eventhandler;

import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageType;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;

public class EuIrcAutoLoginProvider implements AutoLoginProvider {

    @Override
    public boolean supportsNetwork(String server) {
        return server.toLowerCase().contains("euirc.net"); //$NON-NLS-1$
    }



    @Override
    public void requestAuthentification(String forUser, IrcManager irc) {
        irc.sendRawCommand("NICKSERV STATUS " + forUser); //$NON-NLS-1$
    }

    

    @Override
    public boolean processMessageEvent(MessageEvent e, UserManagerImpl users)
            throws AlreadySignedOnException, UnknownUserException {
        if (e.getType() != MessageType.NOTICE) {
            return false;
        }
        if (!e.getUser().getNickName().equalsIgnoreCase("nickserv")) { //$NON-NLS-1$
            return false;
        }
        final String[] parts = e.getMessage().split(" "); //$NON-NLS-1$
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("status") || //$NON-NLS-1$
            !parts[2].equals("3")) { //$NON-NLS-1$
            return false;
        }
        final String forUser = parts[1];
        users.logonWithoutPassword(forUser);
        return true;
    }
    
    
    
    @Override
    public String toString() {
        return "EuIRCAutoLoginProvider"; //$NON-NLS-1$
    }
}
