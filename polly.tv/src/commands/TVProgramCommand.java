package commands;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import core.TVProgram;
import core.TVProgramProvider;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.Types.DateType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class TVProgramCommand extends AbstractTvCommand {

    public TVProgramCommand(MyPolly polly, TVProgramProvider tvProvider) throws DuplicatedSignatureException {
        super(polly, tvProvider, "tvp");
        this.createSignature("Gibt die aktuelle Sendung für den angegebenen Channel und die angegebene Uhrzeit aus.", new StringType(), new DateType());
        this.createSignature("Gibt die aktuelle Sendung für alle angegebenen Channel und die angegebene Uhrzeit aus.", new ListType(new StringType()), new DateType());
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
                Signature signature) throws CommandException {
        Date d = signature.getDateValue(1);
        if (this.match(signature, 0)) {
            String c = signature.getStringValue(0);
            this.replyPrograms(channel, Collections.singletonList(this.tvProvider.getProgram(c, d)));
        } else if (this.match(signature, 1)) {
            List<StringType> cs = signature.getListValue(StringType.class, 0);
            List<TVProgram> channels = new LinkedList<TVProgram>();
            
            for (StringType st : cs) {
                channels.add(this.tvProvider.getProgram(st.getValue(), d));
            }
            this.replyPrograms(channel, channels);
        }
        return false;
    }
}
