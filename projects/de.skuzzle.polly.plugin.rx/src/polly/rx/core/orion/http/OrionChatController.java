package polly.rx.core.orion.http;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import polly.rx.core.orion.OrionChatProvider;
import polly.rx.core.orion.model.DefaultOrionChatEntry;
import polly.rx.core.orion.model.OrionChatEntry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.time.Time;


public class OrionChatController extends PollyController {
    
    public final static String API_ADD_TO_CHAT = "/orion/chat/add"; //$NON-NLS-1$
    public final static String API_REQUEST_CHAT = "/orion/chat/request"; //$NON-NLS-1$
    
    private final OrionChatProvider chatProvider;
    
    public static volatile boolean enableIrcForwarding = true;
    private static volatile String ircForwardChannel = "#regenbogen"; //$NON-NLS-1$
    
    
    
    public OrionChatController(MyPolly myPolly, OrionChatProvider chatProvider) {
        super(myPolly);
        this.chatProvider = chatProvider;
    }
    
    
    

    @Override
    protected Controller createInstance() {
        return new OrionChatController(this.getMyPolly(), chatProvider);
    }
    
    
    
    private final class ChatEntry {
        public String user;
        public String pw;
        public String message;
        public String sender;
        public boolean irc;
    }
    
    
    
    @Post(API_ADD_TO_CHAT)
    public HttpAnswer addChatEntry() throws AlternativeAnswerException {
        final Gson gson = new Gson();
        final ChatEntry ce = gson.fromJson(getEvent().getRequestBody(), ChatEntry.class);
        
        this.checkLogin(ce.user, ce.pw);
        
        final String msg = getServer().esc(ce.message);
        final OrionChatEntry oce = new DefaultOrionChatEntry(ce.sender, msg, 
                Time.currentTime());
        
        try {
            this.chatProvider.addChatEntry(oce, true);
            
            if (ce.irc) {
                this.getMyPolly().irc().sendMessage(ircForwardChannel, 
                    oce.getSender() + ": " + oce.getMessage() + " (via Orion Chat)", this); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
    }
    
    
    
    public final class ChatResult {
        public String[] activeNicks;
        public DefaultOrionChatEntry chat[];
        
        private ChatResult(String[] nicks, DefaultOrionChatEntry chat[]) {
            this.activeNicks = nicks;
            this.chat = chat;
        }
    }
    
    
    
    @Get(API_REQUEST_CHAT)
    public HttpAnswer getchatEntries(@Param("user") String user, 
            @Param("pw") String pw, @Param("max") int max,
            @Param(value = "version", optional = true, defaultValue = "") String version,
            @Param(value = "venad", optional = true, defaultValue = "") String venad,
            @Param(value = "isPoll", optional = true, defaultValue = "false") boolean IsPoll)
                    throws AlternativeAnswerException {
        this.checkLogin(user, pw);
        
        final String nickName = venad.equals("") ? user : venad; //$NON-NLS-1$
        final Gson gson = new GsonBuilder().setDateFormat("HH:mm dd.MM.yyyy").create(); //$NON-NLS-1$
        
        final List<DefaultOrionChatEntry> oces = 
                this.chatProvider.getYoungestEntries(nickName, IsPoll, max);
        
        if (version.equals("")) { //$NON-NLS-1$
            // backward compatibility to script version < 1.5
            return HttpAnswers.newStringAnswer(gson.toJson(oces));
        }
        
        final List<String> activeNicks = new ArrayList<>(
                this.chatProvider.getActiveNicknames());
        activeNicks.addAll(this.getMyPolly().irc()
                .getChannelUser(ircForwardChannel).stream()
                .map(s -> s + " (IRC)") //$NON-NLS-1$
                .collect(Collectors.toList())); 
        
        final DefaultOrionChatEntry[] oceArray = oces.toArray(
                new DefaultOrionChatEntry[oces.size()]);
        final String[] nickArray = activeNicks.toArray(new String[activeNicks.size()]);
        final ChatResult cr = new ChatResult(nickArray, oceArray);
        return HttpAnswers.newStringAnswer(gson.toJson(cr));
    }
}
