package http;

import java.io.File;

import polly.reminds.MyPlugin;
import core.RemindManager;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;


public class RemindHttpController extends PollyController {

    private final RemindManager rm;
    
    public RemindHttpController(MyPolly myPolly, RemindManager rm) {
        super(myPolly);
        this.rm = rm;
    }

    
    
    @Override
    protected Controller createInstance() {
        return new RemindHttpController(this.getMyPolly(), this.rm);
    }

    
    
    @Get(value = "/pages/remindOverview", name = "Overview")
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY,
        "Reminds",
        "List, modify, add and delete your reminds",
        MyPlugin.REMIND_PERMISSION
    })
    public HttpAnswer remindOverview() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        return this.makeAnswer(this.createContext("http/view/remind.overview.html"));
    }
    
}
