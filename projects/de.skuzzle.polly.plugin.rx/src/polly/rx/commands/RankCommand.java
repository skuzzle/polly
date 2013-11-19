package polly.rx.commands;

import java.util.Collections;
import java.util.List;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.ScoreBoardEntry;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.time.Milliseconds;


public class RankCommand extends Command {

    final ScoreBoardManager sbeManager;
    
    
    
    public RankCommand(MyPolly polly, ScoreBoardManager sbeManager) 
            throws DuplicatedSignatureException {
        super(polly, "rank"); //$NON-NLS-1$
        this.createSignature(MSG.rankSig0Desc, 
            MyPlugin.RANK_PERMISSION, new Parameter(MSG.rankSig0Name, Types.STRING));
        this.setHelpText(MSG.rankHelp);
        this.sbeManager = sbeManager;
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature)
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            final String venadName = signature.getStringValue(0); 
            List<ScoreBoardEntry> entries = this.sbeManager.getEntries(venadName);
            
            if (entries.isEmpty()) {
                this.reply(channel, MSG.bind(MSG.rankNoVenad, venadName));
                return false;
            }
            
            Collections.sort(entries, ScoreBoardEntry.BY_DATE);
            
            ScoreBoardEntry oldest = entries.iterator().next();
            ScoreBoardEntry youngest = entries.get(entries.size() - 1);
            long diff = Math.abs(youngest.getDate().getTime() - oldest.getDate().getTime());
            long days = Milliseconds.toDays(diff);
            int pointDiff = youngest.getPoints() - oldest.getPoints();
            double pointsPerDay = (double) pointDiff / (double)days;

            final String result = MSG.bind(MSG.rankSuccess, youngest.getRank(), 
                    youngest.getPoints(), 
                    this.getMyPolly().formatting().formatNumber(pointsPerDay),
                    this.getMyPolly().formatting().formatTimeSpanMs(diff),
                    this.getMyPolly().formatting().formatDate(youngest.getDate()));

            this.reply(channel, result);
        }
        return false;
    }
}
