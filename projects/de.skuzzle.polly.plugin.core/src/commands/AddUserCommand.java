package commands;

import java.util.Map;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InvalidUserNameException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import de.skuzzle.polly.sdk.resources.PollyBundle;
import de.skuzzle.polly.sdk.resources.Resources;

public class AddUserCommand extends Command {
    
    private final String HELP = "add.user.help";
    private final String SIG0_DESC = "add.user.sig0.desc";
    private final String SIG0_PARAM0 = "add.user.sig0.username";
    private final String SIG0_PARAM1 = "add.user.sig0.password";
    private final String QRY_ONLY = "add.user.qryonly";
    private final String SUCCESS = "add.user.success";
    private final String EXISTS = "add.user.exists";
    private final String INVALID = "add.user.invalid";
    private final String FAIL = "add.user.fail";
    
    
    private final static PollyBundle MSG = Resources.get(MyPlugin.FAMILY);

    
    public AddUserCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "adduser");
        this.createSignature(MSG.get(SIG0_DESC), 
            MyPlugin.ADD_USER_PERMISSION,
            new Parameter(MSG.get(SIG0_PARAM0), Types.USER),
            new Parameter(MSG.get(SIG0_PARAM1), Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(MSG.get(HELP));
        this.setQryCommand(true);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        return true;
    }

    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        this.reply(channel, MSG.get(QRY_ONLY));
    }
    
    
    
    @Override
    public void renewConstants(Map<String, Types> map) {
        map.put("ADMIN", new NumberType(UserManager.ADMIN));
        map.put("MEMBER", new NumberType(UserManager.MEMBER));
        map.put("REG", new NumberType(UserManager.REGISTERED));
        map.put("UNKNOWN", new NumberType(UserManager.UNKNOWN));
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String password = signature.getStringValue(1);
            
            try {
                this.getMyPolly().users().addUser(userName, password);
                this.reply(executer, MSG.get(SUCCESS, userName));
            } catch (UserExistsException e) {
                this.reply(executer, MSG.get(EXISTS, userName));
            } catch (DatabaseException e)  {
                this.reply(executer, MSG.get(FAIL));
            } catch (InvalidUserNameException e) {
                this.reply(executer, MSG.get(INVALID, userName));
            }
        }
    }
}
