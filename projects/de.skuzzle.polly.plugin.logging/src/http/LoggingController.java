package http;

import java.util.Map;

import polly.logging.MSG;
import polly.logging.MyPlugin;
import core.PollyLoggingManager;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.resources.Resources;
import de.skuzzle.polly.sdk.resources.PollyBundle;

public class LoggingController extends PollyController {
    
    public final static String LOGGING_PAGE = "/pages/showLogs"; //$NON-NLS-1$
    public final static String LOGGING_PAGE_CONTENT = "/http/view/logs.overview.html"; //$NON-NLS-1$

    public final static String REPLAY_PAGE = "/pages/replay"; //$NON-NLS-1$
    public final static String REPLAY_PAGE_CONTENT = "/http/view/replay.overview.html"; //$NON-NLS-1$
    
    private final static String LOG_CATEGORY_KEY = "httpLoggingCategory"; //$NON-NLS-1$
    private final static String SEARCH_LOGS_NAME_KEY = "httpSearchLogs"; //$NON-NLS-1$
    private final static String SEARCH_LOGS_DESC_KEY = "httpSearchLogsDesc"; //$NON-NLS-1$
    
    private final static String REPLAY_NAME_KEY = "httpReplay"; //$NON-NLS-1$
    private final static String REPLAY_DESC_KEY = "httpReplayDesc"; //$NON-NLS-1$
    
    
    
    private final PollyLoggingManager lm;



    public LoggingController(MyPolly myPolly, PollyLoggingManager lm) {
        super(myPolly);
        this.lm = lm;
    }

    
    
    @Override
    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = super.createContext(content);
        final PollyBundle pb = Resources.get(MSG.FAMILY);
        c.put("MSG", pb); //$NON-NLS-1$
        return c;
    }


    
    @Override
    protected Controller createInstance() {
        return new LoggingController(this.getMyPolly(), this.lm);
    }



    @Get(value = LOGGING_PAGE, name = SEARCH_LOGS_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY,
        MSG.FAMILY,
        LOG_CATEGORY_KEY, 
        SEARCH_LOGS_DESC_KEY,
        MyPlugin.CHANNEL_LOG_PERMISSION 
    })
    public HttpAnswer loggingPage() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.CHANNEL_LOG_PERMISSION);
        return this.makeAnswer(this.createContext(LOGGING_PAGE_CONTENT));
    }



    @Get(value = REPLAY_PAGE, name = REPLAY_NAME_KEY)
    @OnRegister({ 
        WebinterfaceManager.ADD_MENU_ENTRY, 
        MSG.FAMILY,
        LOG_CATEGORY_KEY,
        REPLAY_DESC_KEY, 
        MyPlugin.REPLAY_PERMISSION 
    })
    public HttpAnswer replayPage() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REPLAY_PERMISSION);
        return this.makeAnswer(this.createContext(REPLAY_PAGE_CONTENT));
    }
}
