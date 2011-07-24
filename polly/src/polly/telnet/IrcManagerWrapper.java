package polly.telnet;

import java.util.List;
import java.util.Set;

import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.JoinPartListener;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;
import de.skuzzle.polly.sdk.eventlistener.QuitListener;


public class IrcManagerWrapper implements IrcManager {

    private IrcManager ircManager;
    private TelnetConnection connection;
    
    public IrcManagerWrapper(IrcManager other, TelnetConnection connection) {
        this.ircManager = other;
        this.connection = connection;
    }
    
    @Override
    public void quit(String message) {
        this.ircManager.quit(message);
    }

    @Override
    public void quit() {
        this.ircManager.quit();
    }

    @Override
    public boolean isOnline(String nickName) {
        return this.ircManager.isOnline(nickName);
    }

    @Override
    public Set<String> getOnlineUsers() {
        return this.ircManager.getOnlineUsers();
    }

    @Override
    public void disconnect() {
        this.ircManager.disconnect();
    }

    @Override
    public boolean isConnected() {
        return this.ircManager.isConnected();
    }

    @Override
    public List<String> getChannels() {
        return this.ircManager.getChannels();
    }

    @Override
    public boolean isOnChannel(String channel, String nickName) {
        return this.ircManager.isOnChannel(channel, nickName);
    }

    @Override
    public List<String> getChannelUser(String channel) {
        return this.ircManager.getChannelUser(channel);
    }

    @Override
    public void joinChannel(String channel, String password) {
        this.ircManager.joinChannel(channel, password);
    }

    @Override
    public void joinChannels(String... channels) {
        this.ircManager.joinChannels(channels);
    }

    @Override
    public void partChannel(String channel, String message) {
        this.partChannel(channel, message);
    }

    @Override
    public void kick(String channel, String nickName, String reason) {
        this.ircManager.kick(channel, nickName, reason);
    }

    @Override
    public void op(String channel, String nickName) {
        this.ircManager.op(channel, nickName);
    }

    @Override
    public void deop(String channel, String nickName) {
        this.ircManager.deop(channel, nickName);
    }

    @Override
    public void sendMessage(String channel, String message) {
        this.ircManager.sendMessage(channel, message);
        this.connection.send(message);
    }

    @Override
    public void sendAction(String channel, String message) {
        this.ircManager.sendAction(channel, message);
        this.connection.send(message);        
    }

    @Override
    public void setTopic(String channel, String topic) {
        this.ircManager.setTopic(channel, topic);
    }

    @Override
    public String getTopic(String channel) {
        return this.ircManager.getTopic(channel);
    }

    @Override
    public void addNickChangeListener(NickChangeListener listener) {
        this.ircManager.addNickChangeListener(listener);
    }

    @Override
    public void removeNickChangeListener(NickChangeListener listener) {
        this.ircManager.removeNickChangeListener(listener);
    }

    @Override
    public void addJoinPartListener(JoinPartListener listener) {
        this.ircManager.addJoinPartListener(listener);
    }

    @Override
    public void removeJoinPartListener(JoinPartListener listener) {
        this.ircManager.removeJoinPartListener(listener);
    }

    @Override
    public void addQuitListener(QuitListener listener) {
        this.ircManager.addQuitListener(listener);
    }

    @Override
    public void removeQuitListener(QuitListener listener) {
        this.ircManager.removeQuitListener(listener);
    }

    @Override
    public void addMessageListener(MessageListener listener) {
        this.ircManager.addMessageListener(listener);
    }

    @Override
    public void removeMessageListener(MessageListener listener) {
        this.ircManager.removeMessageListener(listener);
    }
}
