package polly.core;


import commands.AddTrainCommand;
import commands.AddUserCommand;
import commands.AliasCommand;
import commands.CalendarCommand;
import commands.CloseTrainCommand;
import commands.ClumBombCommand;
import commands.DefineCommand;
import commands.DelVarCommand;
import commands.DeleteUserCommand;
import commands.DeliverTrainCommand;
import commands.DictCommand;
import commands.DitoCommand;
import commands.ExportAttributesCommand;
import commands.FooCommand;
import commands.GetAttributeCommand;
import commands.GhostCommand;
import commands.GooglePicsCommand;
import commands.GreetingCommand;
import commands.HighlightModeCommand;
import commands.HopCommand;
import commands.IsDownCommand;
import commands.JoinCommand;
import commands.KickCommand;
import commands.ListAttributesCommand;
import commands.LmgtfyCommand;
import commands.AnyficationCommand;
import commands.MyTrainsCommand;
import commands.MyVenadCommand;
import commands.PartCommand;
import commands.QuitCommand;
import commands.RawIrcCommand;
import commands.RegisterCommand;
import commands.ReloadConfigCommand;
import commands.RestartCommand;
import commands.SetAttributeCommand;
import commands.SetMyPasswordCommand;
import commands.SetPasswordCommand;
import commands.SetUserLevelCommand;
import commands.ShowCommandsCommand;
import commands.InfoCommand;
import commands.SignOffCommand;
import commands.AuthCommand;
import commands.TalkCommand;
import commands.UptimeCommand;
import commands.UsersCommand;
import commands.VarCommand;
import commands.VenadCommand;
import commands.VersionCommand;
import commands.WikiCommand;
import commands.roles.AssignPermissionCommand;
import commands.roles.AssignRoleCommand;
import commands.roles.CreateRoleCommand;
import commands.roles.DeleteRoleCommand;
import commands.roles.ListPermissionsCommand;
import commands.roles.ListRolesCommand;
import commands.roles.RemovePermissionCommand;
import commands.roles.RemoveRoleCommand;
import core.ForwardHighlightHandler;
import core.GreetDeliverer;
import core.HighlightReplyHandler;
import core.JoinTimeCollector;
import core.TrainManager;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.constraints.Constraints;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
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

    public final static String GREETING = "GREETING";
    public final static String VENAD = "VENAD";
    
    public final static String FORWARD_HIGHLIGHTS = "FORWARD_HIGHLIGHTS";
    public final static String DEFAULT_FORWARD_HIGHLIGHTS = "false";
    
    private TrainManager trainManager;
    private GreetDeliverer greetDeliverer;
    private HighlightReplyHandler highlightHandler;
    private JoinTimeCollector joinTimeCollector;
    private MessageListener highlightForwarder;
    
    
	public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
	            DuplicatedSignatureException {
	    
		super(myPolly);
		
		this.addCommand(new AnyficationCommand(myPolly));
		
		//this.topicManager = new TopicManager(myPolly);
		this.trainManager = new TrainManager(myPolly);
        this.getMyPolly().persistence().registerEntity(TopicEntity.class);
        this.getMyPolly().persistence().registerEntity(TrainEntity.class);
        
        this.greetDeliverer = new GreetDeliverer(myPolly);
        this.getMyPolly().users().addUserListener(this.greetDeliverer);
        this.addCommand(new GreetingCommand(myPolly));
        
        this.addCommand(new AliasCommand(myPolly));
		this.addCommand(new InfoCommand(myPolly));
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
		this.addCommand(new DefineCommand(myPolly));
		this.addCommand(new LmgtfyCommand(myPolly));
		this.addCommand(new DictCommand(myPolly));
		this.addCommand(new GooglePicsCommand(myPolly));
		this.addCommand(new IsDownCommand(myPolly));
		
		this.addCommand(new UsersCommand(myPolly));
		this.addCommand(new AuthCommand(myPolly));
		this.addCommand(new SignOffCommand(myPolly));
		this.addCommand(new GhostCommand(myPolly));
		this.addCommand(new AddUserCommand(myPolly));
		this.addCommand(new DeleteUserCommand(myPolly));
		this.addCommand(new SetMyPasswordCommand(myPolly));
		this.addCommand(new SetPasswordCommand(myPolly));
		this.addCommand(new SetUserLevelCommand(myPolly));
		this.addCommand(new RegisterCommand(myPolly));
		this.addCommand(new RawIrcCommand(myPolly));
		this.addCommand(new SetAttributeCommand(myPolly));
		this.addCommand(new ListAttributesCommand(myPolly));
		this.addCommand(new GetAttributeCommand(myPolly));
		this.addCommand(new ExportAttributesCommand(myPolly));
		
		
		
		this.addCommand(new AssignRoleCommand(myPolly));
		this.addCommand(new RemoveRoleCommand(myPolly));
		this.addCommand(new CreateRoleCommand(myPolly));
		this.addCommand(new DeleteRoleCommand(myPolly));
		this.addCommand(new ListRolesCommand(myPolly));
		this.addCommand(new AssignPermissionCommand(myPolly));
		this.addCommand(new RemovePermissionCommand(myPolly));
		this.addCommand(new ListPermissionsCommand(myPolly));
		
		
		
		this.joinTimeCollector = new JoinTimeCollector(myPolly.getTimeProvider());
		this.joinTimeCollector.addTo(myPolly.irc());
		this.addCommand(new UptimeCommand(myPolly, this.joinTimeCollector));
		

		this.addCommand(new FooCommand(myPolly));
		this.addCommand(new VarCommand(myPolly));
		this.addCommand(new DelVarCommand(myPolly));
		
		this.addCommand(new VenadCommand(myPolly));
		this.addCommand(new MyVenadCommand(myPolly));
		//this.addCommand(new AddTopicCommand(myPolly, this.topicManager));
		
		this.addCommand(new AddTrainCommand(myPolly, this.trainManager));
		this.addCommand(new CloseTrainCommand(myPolly, this.trainManager));
		this.addCommand(new MyTrainsCommand(myPolly, this.trainManager));
		this.addCommand(new DeliverTrainCommand(myPolly, this.trainManager));
		
		this.addCommand(new RestartCommand(myPolly));
		this.addCommand(new ReloadConfigCommand(myPolly));
		
		this.addCommand(new DitoCommand(myPolly));
		
		this.highlightHandler = new HighlightReplyHandler(myPolly.getTimeProvider());
		myPolly.irc().addMessageListener(this.highlightHandler);
		this.addCommand(new HighlightModeCommand(myPolly, this.highlightHandler));
		
		this.highlightForwarder = new ForwardHighlightHandler(myPolly.mails(), 
		    myPolly.users());
		myPolly.irc().addMessageListener(this.highlightForwarder);
	}
	
	
	
	@Override
	public void onLoad() {
	    //this.topicManager.loadAll();
	    /*Calendar c = Calendar.getInstance();
	    c.set(Calendar.DAY_OF_MONTH, 25);
	    TopicEntity test = new TopicEntity("C0mb4t", "#debugging", "Noch %ld% Tage bis ka", "Nu isses soweit", c.getTime());
	    this.topicManager.addTopicTask(test);*/
	    
	    try {
	        this.getMyPolly().users().addAttribute(VENAD, "<unbekannt>");
	        this.getMyPolly().users().addAttribute(GREETING, "");
	        this.getMyPolly().users().addAttribute("AZ", "0", Constraints.INTEGER);
	        this.getMyPolly().users().addAttribute(FORWARD_HIGHLIGHTS, 
	            DEFAULT_FORWARD_HIGHLIGHTS, Constraints.BOOLEAN);
	    } catch (Exception ignore) {
	        ignore.printStackTrace();
	    }
	}

	
	
	@Override
	protected void actualDispose() throws DisposingException {
	    super.actualDispose();
	    //this.topicManager.dispose();
	    this.getMyPolly().users().removeUserListener(this.greetDeliverer);
	    this.getMyPolly().irc().removeMessageListener(this.highlightHandler);
	    this.getMyPolly().irc().removeMessageListener(this.highlightForwarder);
	    this.joinTimeCollector.remove(this.getMyPolly().irc());
	}
}