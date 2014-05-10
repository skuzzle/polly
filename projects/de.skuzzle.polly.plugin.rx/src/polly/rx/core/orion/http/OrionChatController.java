package polly.rx.core.orion.http;

import java.util.List;

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
        
        final OrionChatEntry oce = new DefaultOrionChatEntry(ce.sender, ce.message, 
                Time.currentTime());
        
        try {
            this.chatProvider.addChatEntry(oce);
            
            if (ce.irc) {
                this.getMyPolly().irc().sendMessage(ircForwardChannel, 
                    oce.getSender() + ": " + oce.getMessage() + " (via Orion Chat)", this); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
    }
    
    
    
    @Get(API_REQUEST_CHAT)
    public HttpAnswer getchatEntries(@Param("user")String user, 
            @Param("pw") String pw, @Param("max") int max) throws AlternativeAnswerException {
        this.checkLogin(user, pw);
        final Gson gson = new GsonBuilder().setDateFormat("HH:mm dd.MM.yyyy").create(); //$NON-NLS-1$
        final List<? extends OrionChatEntry> oces = 
                this.chatProvider.getYoungestEntries(max);
        return HttpAnswers.newStringAnswer(gson.toJson(oces));
    }
}
