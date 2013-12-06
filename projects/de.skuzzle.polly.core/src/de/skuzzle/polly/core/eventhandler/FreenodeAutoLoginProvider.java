package de.skuzzle.polly.core.eventhandler;

import org.jibble.pircbot.Colors;

import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageType;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;


public class FreenodeAutoLoginProvider implements AutoLoginProvider {
    
    private final static String INFORMATION_OF = "information on "; //$NON-NLS-1$
    
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
        final int i = e.getMessage().toLowerCase().indexOf(INFORMATION_OF);
        if (i != -1) {
            final int start = i + INFORMATION_OF.length();
            final int j = e.getMessage().indexOf(" ", start + 1); //$NON-NLS-1$
            if (j < 0) {
                return false;
            }
            final String forUser = Colors.removeFormattingAndColors(
                    e.getMessage().substring(start, j));
            users.logonWithoutPassword(forUser);
            return true;
        }
        return false;
    }
    
    
    
    @Override
    public String toString() {
        return "FreenodeAutoLoginProvider"; //$NON-NLS-1$
    }
}
