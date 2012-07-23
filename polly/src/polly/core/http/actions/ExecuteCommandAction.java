package polly.core.http.actions;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class ExecuteCommandAction extends HttpAction {

    private final static String SIGNATURE_PARAM = "signature";
    private final static String REDIRECT_PARAM = "redirect";
    
    private CommandManager commandManager;
    
    
    public ExecuteCommandAction(CommandManager commandManager) {
        super("/exec");
        this.commandManager = commandManager;
    }

    
    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        try {
            String input = e.getProperty(SIGNATURE_PARAM);
            boolean redirect = e.getProperty(REDIRECT_PARAM) != null;
            
            Signature actual = this.commandManager.signatureFromString(
                e.getSession().getUser(), input, "");
            Command cmd = this.commandManager.getCommand(actual);
            if (cmd.isHttpEnabled()) {
                throw new Exception("This command is not http enabled");
            }
            
            HttpTemplateContext c = cmd.doExecuteHttp(
                e.getSession().getUser(), actual, e);
            context.putAll(c);
            if (!redirect) {
                context.setTemplate(e.getProperty(REDIRECT_PARAM));
            }
        } catch (Exception e1) {
            context.setTemplate("webinterface/pages/error.html");
            context.put("errorText", e1.getMessage());
        }
    }
}
