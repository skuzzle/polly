package de.skuzzle.polly.sdk.http;


public abstract class SecureHttpAction extends HttpAction {

    private int userLevel;
    
    public SecureHttpAction(String name, int userLevel) {
        super(name);
    }
    
    
    
    public abstract void doExecute(HttpEvent e, HttpTemplateContext context);
    
    

    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        if (e.getSession().getUser() == null || e.getSession().getUser().getUserLevel() < this.userLevel) {
            context.setTemplate("webinterface/pages/error.html");
            context.put("errorText", "Insuficcient rights");
        } else {
            this.doExecute(e, context);
        }
    }

}
