package de.skuzzle.polly.core.internal.httpv2;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.tools.collections.TemporaryValueMap;
import de.skuzzle.polly.tools.events.OneTimeEventListener;

public class PasswordRecoverer {

    private class CodeListener extends MessageAdapter implements OneTimeEventListener {

        private final String nickName;
        private AtomicBoolean done = new AtomicBoolean();



        public CodeListener(String nickName) {
            this.nickName = nickName;
        }



        @Override
        public void privateMessage(MessageEvent e) {
            final String nick = e.getUser().getNickName().toLowerCase();
            if (nick.equals(this.nickName)) {
                this.done.set(true);
                final String code = e.getMessage();
                if (PasswordRecoverer.this.verifyRecovery(nick, code)) {
                    
                }
            }
        }



        @Override
        public boolean workDone() {
            return done.get();
        }
    }
    
    

    private final static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; //$NON-NLS-1$
    private final static int CODE_LENGTH = 6;
    private final static long RECOVERY_TIMEOUT = Milliseconds.fromSeconds(30);
    private final static Random RANDOM = new Random();

    private final TemporaryValueMap<String, String> recoverySessions;



    public PasswordRecoverer() {
        this.recoverySessions = new TemporaryValueMap<>(RECOVERY_TIMEOUT);
    }



    public synchronized String startRecovery(IrcManager irc, String nickName) {
        final String code = this.generateCode();
        this.recoverySessions.put(nickName.toLowerCase(), code);
        irc.sendMessage(nickName, "Verification Code: " + code, this); //$NON-NLS-1$
        return code;
    }



    public boolean verifyRecovery(String nickName, String code) {
        final String expected = this.recoverySessions.remove(nickName.toLowerCase());
        if (expected == null) {
            return false;
        }
        return code.equals(expected);
    }



    private String generateCode() {
        final StringBuilder b = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; ++i) {
            final int idx = RANDOM.nextInt(CHARS.length());
            final char c = RANDOM.nextBoolean() 
                    ? CHARS.charAt(idx) 
                    : Character.toLowerCase(CHARS.charAt(idx));
            b.append(c);
        }
        return b.toString();
    }
}
