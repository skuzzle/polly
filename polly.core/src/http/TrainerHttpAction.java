package http;

import polly.core.MyPlugin;
import core.TrainManagerV2;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import entities.TrainEntityV2;

public class TrainerHttpAction extends HttpAction {

    private TrainManagerV2 trainManager;
    
    
    public TrainerHttpAction(MyPolly myPolly, TrainManagerV2 trainManager) {
        super("/Trainer", myPolly);
        this.trainManager = trainManager;
        this.permissions.add(MyPlugin.ADD_TRAIN_PERMISSION);
        this.permissions.add(MyPlugin.CLOSE_TRAIN_PERMISSION);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e)
            throws HttpTemplateException {

        HttpTemplateContext c = new HttpTemplateContext("pages/trainer.html");
        String action = e.getProperty("action");
        
        if (action != null && action.equals("closeTrain")) {
            int trainId = Integer.parseInt(e.getProperty("trainId"));
            
            try {
                this.trainManager.closeOpenTrain(e.getSession().getUser(), trainId);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            } catch (CommandException e1) {
                e.throwTemplateException(e1);
            }
        } else if (action != null && action.equals("addTrain")) {
            String forUser = e.getProperty("forUser");
            String paste = e.getProperty("paste");
            double factor = Double.parseDouble(e.getProperty("factor"));
            
            if (forUser == null | forUser.equals("") || paste == null || 
                    paste.equals("")) {
                e.throwTemplateException("Invalid Train Information", 
                        "Please fill out all fields");
            }
            
            try {
                    TrainEntityV2 te = TrainEntityV2.parseString(
                    e.getSession().getUser().getId(), forUser, factor, paste);
                this.trainManager.addTrain(te);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        }
        
        c.put("allOpen", this.trainManager.getOpenTrains(e.getSession().getUser()));
        c.put("allClosed", this.trainManager.getClosedTrains(e.getSession().getUser()));
        c.put("trainManager", this.trainManager);
        return c;
    }

}
