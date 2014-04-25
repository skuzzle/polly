package polly.annoyingPeople;

import polly.annoyingPeople.commands.AddAnnoyingPersonCommand;
import polly.annoyingPeople.entities.AnnoyingPerson;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;


public class MyPlugin extends PollyPlugin {
    
    public final static String PERMISSION_ADD_ANNOYING_PERSON = "polly.permission.ADD_ANNOYING_PERSON"; //$NON-NLS-1$
    
    private final PeopleAnnoyer annoyer;
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
            DuplicatedSignatureException {
        super(myPolly);
        
        myPolly.persistence().registerEntity(AnnoyingPerson.class);
        
        PersonManager personManager = new DBPersonManager(myPolly.persistence());
        this.annoyer = new PeopleAnnoyer(personManager, myPolly.irc());
        this.addDisposable(annoyer);
        
        myPolly.irc().addJoinPartListener(annoyer);
        personManager.addPersonListener(annoyer);
        
        this.addCommand(new AddAnnoyingPersonCommand(myPolly, personManager));
    }

    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        this.getMyPolly().irc().removeJoinPartListener(this.annoyer);
    }

}
