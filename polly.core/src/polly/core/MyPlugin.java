package polly.core;


import commands.AddTrainCommand;
import commands.AddUserCommand;
import commands.CalendarCommand;
import commands.CloseTrainCommand;
import commands.ClumBombCommand;
import commands.DelVarCommand;
import commands.DeleteUserCommand;
import commands.DeliverTrainCommand;
import commands.DictCommand;
import commands.FooCommand;
import commands.HelpCommand;
import commands.HopCommand;
import commands.InspectCommand;
import commands.JoinCommand;
import commands.KickCommand;
import commands.LmgtfyCommand;
import commands.MyTrainsCommand;
import commands.MyVenadCommand;
import commands.PartCommand;
import commands.QuitCommand;
import commands.RegisterCommand;
import commands.RestartCommand;
import commands.SetMyPasswordCommand;
import commands.SetPasswordCommand;
import commands.SetUserLevelCommand;
import commands.ShowCommandsCommand;
import commands.InfoCommand;
import commands.SignOffCommand;
import commands.AuthCommand;
import commands.TalkCommand;
import commands.VarCommand;
import commands.VenadCommand;
import commands.VersionCommand;
import commands.WikiCommand;
import core.TopicManager;
import core.TrainManager;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import entities.TopicEntity;
import entities.TrainEntity;


/**
 * 
 * @author Simon
 * @version 27.07.2011 3851c1b
 */
public class MyPlugin extends PollyPlugin {

    private TopicManager topicManager;
    private TrainManager trainManager;
    
	public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, DuplicatedSignatureException {
		super(myPolly);
		
		//this.topicManager = new TopicManager(myPolly);
		this.trainManager = new TrainManager(myPolly);
        this.getMyPolly().persistence().registerEntity(TopicEntity.class);
        this.getMyPolly().persistence().registerEntity(TrainEntity.class);
        
		this.addCommand(new InfoCommand(myPolly));
		this.addCommand(new HelpCommand(myPolly));
		this.addCommand(new QuitCommand(myPolly));
		this.addCommand(new JoinCommand(myPolly));
		this.addCommand(new PartCommand(myPolly));
		this.addCommand(new HopCommand(myPolly));
		this.addCommand(new KickCommand(myPolly));
		this.addCommand(new ClumBombCommand(myPolly));
		this.addCommand(new TalkCommand(myPolly));
		this.addCommand(new VersionCommand(myPolly));
		this.addCommand(new ShowCommandsCommand(myPolly));
		this.addCommand(new CalendarCommand(myPolly));
		
		this.addCommand(new WikiCommand(myPolly));
		this.addCommand(new LmgtfyCommand(myPolly));
		this.addCommand(new DictCommand(myPolly));
		
		this.addCommand(new AuthCommand(myPolly));
		this.addCommand(new SignOffCommand(myPolly));
		this.addCommand(new AddUserCommand(myPolly));
		this.addCommand(new DeleteUserCommand(myPolly));
		this.addCommand(new SetMyPasswordCommand(myPolly));
		this.addCommand(new SetPasswordCommand(myPolly));
		this.addCommand(new SetUserLevelCommand(myPolly));
		this.addCommand(new RegisterCommand(myPolly));

		this.addCommand(new FooCommand(myPolly));
		this.addCommand(new VarCommand(myPolly));
		this.addCommand(new DelVarCommand(myPolly));
		this.addCommand(new InspectCommand(myPolly));
		
		this.addCommand(new VenadCommand(myPolly));
		this.addCommand(new MyVenadCommand(myPolly));
		//this.addCommand(new AddTopicCommand(myPolly, this.topicManager));
		
		this.addCommand(new AddTrainCommand(myPolly, this.trainManager));
		this.addCommand(new CloseTrainCommand(myPolly, this.trainManager));
		this.addCommand(new MyTrainsCommand(myPolly, this.trainManager));
		this.addCommand(new DeliverTrainCommand(myPolly, this.trainManager));
		
		this.addCommand(new RestartCommand(myPolly));
	}
	
	
	
	@Override
	public void onLoad() {
	    //this.topicManager.loadAll();
	    /*Calendar c = Calendar.getInstance();
	    c.set(Calendar.DAY_OF_MONTH, 25);
	    TopicEntity test = new TopicEntity("C0mb4t", "#debugging", "Noch %ld% Tage bis ka", "Nu isses soweit", c.getTime());
	    this.topicManager.addTopicTask(test);*/
	    
	    try {
	        this.getMyPolly().users().addAttribute("VENAD", "<unbekannt>");
	    } catch (Exception ignore){}
	}

	
	
	@Override
	protected void actualDispose() throws DisposingException {
	    super.actualDispose();
	    this.topicManager.dispose();
	}
}