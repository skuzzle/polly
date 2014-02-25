package polly.rx.core.orion;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;

public class VenadUserMapper {
    
    private final static String UNKNOWN_VENAD = MSG.reportShipModelUnknown;

    private final UserManager userManager;



    public VenadUserMapper(UserManager userManager) {
        this.userManager = userManager;
    }



    public String venadFromIRC(String nickName) {
        final IrcUser ircuser = new IrcUser(nickName, "", ""); //$NON-NLS-1$//$NON-NLS-2$
        final User user = this.userManager.getUser(ircuser);
        return this.venadFromUser(user);
    }



    public String venadFromIRC(IrcUser user) {
        final User u = this.userManager.getUser(user);
        return this.venadFromUser(u);
    }



    public String venadFromUser(String userName) {
        final User user = this.userManager.getUser(userName);
        return this.venadFromUser(user);
    }



    public String venadFromUser(User user) {
        if (user == null) {
            return UNKNOWN_VENAD;
        }
        final StringType str = (StringType) user.getAttribute(MyPlugin.VENAD);
        if (str == null || str.getValue().isEmpty()) {
            return UNKNOWN_VENAD;
        }
        return str.getValue();
    }
}