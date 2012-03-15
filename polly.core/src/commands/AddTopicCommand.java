package commands;

import java.util.Date;

import core.TopicManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.TopicEntity;

public class AddTopicCommand extends Command {

    private TopicManager topicManager;
    
    public AddTopicCommand(MyPolly polly, TopicManager topicManager) throws DuplicatedSignatureException {
        super(polly, "topic");
        this.topicManager = topicManager;
        //this.createSignature("Setzt", new DateType(), new StringType(), new StringType());
    }

    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        return true;
    }
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        if (this.match(signature, 0)) {
            Date dueDate = signature.getDateValue(0);
            String pattern = signature.getStringValue(1);
            String after = signature.getStringValue(2);
            if (after.equals("%old%")) {
                after = this.getMyPolly().irc().getTopic(channel);
            }
            TopicEntity te = new TopicEntity(executer.getCurrentNickName(), channel, pattern, after, dueDate);
            this.topicManager.addTopicTask(te);
        }
    }
}
