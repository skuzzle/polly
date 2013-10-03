package http;

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

public class LoggingController extends PollyController {

    private final PollyLoggingManager lm;



    public LoggingController(MyPolly myPolly, PollyLoggingManager lm) {
        super(myPolly);
        this.lm = lm;
    }



    @Override
    protected Controller createInstance() {
        return new LoggingController(this.getMyPolly(), this.lm);
    }



    @Get(value = "/pages/showLogs", name = "Search logs")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Logging", "List IRC logs",
            MyPlugin.CHANNEL_LOG_PERMISSION })
    public HttpAnswer loggingPage() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.CHANNEL_LOG_PERMISSION);
        return this.makeAnswer(this.createContext("/http/view/logs.overview.html"));
    }



    @Get(value = "/pages/replay", name = "Replay")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Logging",
            "Replay of missed IRC messages", MyPlugin.REPLAY_PERMISSION })
    public HttpAnswer replayPage() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REPLAY_PERMISSION);
        return this.makeAnswer(this.createContext("/http/view/replay.overview.html"));
    }
}
