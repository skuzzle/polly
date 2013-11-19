package de.skuzzle.polly.sdk;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;


public interface User extends Comparable<User> {
    
    public final static Comparator<User> BY_ID = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return o1.getId() - o2.getId();
        }
    };
    
    public final static Comparator<User> BY_NAME = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
    
    public final static Comparator<User> BY_NICKNAME = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            final String n1 = o1.getCurrentNickName() == null ? "" : o1.getCurrentNickName(); //$NON-NLS-1$
            final String n2 = o2.getCurrentNickName() == null ? "" : o2.getCurrentNickName(); //$NON-NLS-1$
            return n1.compareToIgnoreCase(n2);
        }
    };
    
    public final static Comparator<User> BY_ISIDLE = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return new Boolean(o1.isIdle()).compareTo(o2.isIdle());
        }
    };
    
    public final static Comparator<User> BY_LAST_ACTION = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return new Date(o1.getLastMessageTime()).compareTo(
                new Date(o2.getLastMessageTime()));
        }
    };
    
    public final static Comparator<User> BY_LOGIN = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return new Date(o1.getLoginTime()).compareTo(
                new Date(o2.getLoginTime()));
        }
    };
    
    /**
     * Time in milliseconds after which a user is considered to be idle
     */
    public final static long IDLE_AFTER = 60 * 1000 * 30; // 30 min

    public abstract int getId();
    
    public abstract boolean checkPassword(String password);
    
    public abstract String getHashedPassword();
    
    public abstract void setHashedPassword(String password);
    
    public abstract void setPassword(String password);
    
    public abstract String getName();
    
    public abstract void setName(String name);
    
    public abstract String getCurrentNickName();
    
    public abstract void setCurrentNickName(String nickName);
    
    public abstract Set<String> getAttributeNames();
    
    public abstract Types getAttribute(String name);
    
    public abstract boolean isIdle();
    
    public abstract long getLastMessageTime();
    
    public abstract void setLastMessageTime(long lastIdleTime);
    
    public abstract long getLastIdleTime();
    
    public abstract long getLoginTime();

    public boolean isPollyAdmin();
}
