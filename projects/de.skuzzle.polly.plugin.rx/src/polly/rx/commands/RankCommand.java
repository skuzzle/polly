package polly.rx.commands;

import java.util.Collections;
import java.util.List;

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
        super(polly, "rank");
        this.createSignature("Zeigt Rang und Punkte eines Venads", 
            MyPlugin.RANK_PERMISSION, new Parameter("Venadname", Types.STRING));
        this.setHelpText("Zeigt Rang und Punkte eines Venads");
        this.sbeManager = sbeManager;
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature)
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            final String venadName = signature.getStringValue(0); 
            List<ScoreBoardEntry> entries = this.sbeManager.getEntries(venadName);
            
            if (entries.isEmpty()) {
                this.reply(channel, "Kein Eintrag für den Venad " + 
                    venadName + " vorhanden");
                return false;
            }
            
            Collections.sort(entries, ScoreBoardEntry.BY_DATE);
            
            ScoreBoardEntry oldest = entries.iterator().next();
            ScoreBoardEntry youngest = entries.get(entries.size() - 1);
            long diff = Math.abs(youngest.getDate().getTime() - oldest.getDate().getTime());
            long days = Milliseconds.toDays(diff);
            int pointDiff = youngest.getPoints() - oldest.getPoints();
            double pointsPerDay = (double) pointDiff / (double)days;

            final StringBuilder b = new StringBuilder();
            b.append("Rang: ");
            b.append(youngest.getRank());
            b.append(", Punkte: ");
            b.append(youngest.getPoints());
            b.append(" (");
            b.append(this.getMyPolly().formatting().formatNumber(pointsPerDay));
            b.append(" pro Tag innerhalb von ");
            b.append(this.getMyPolly().formatting().formatTimeSpanMs(diff));
            b.append("), Daten vom: ");
            b.append(this.getMyPolly().formatting().formatDate(youngest.getDate()));

            this.reply(channel, b.toString());
        }
        return false;
    }
}
