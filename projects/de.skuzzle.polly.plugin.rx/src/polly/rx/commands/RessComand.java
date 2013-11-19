package polly.rx.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.core.ResourcePriceGrabber;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class RessComand extends Command {
    
    private final int REFRESH_THRESHOLD = 1;
    
    private final ResourcePriceGrabber rpgrabber;
    
    
    public RessComand(MyPolly myPolly) throws DuplicatedSignatureException {
        super(myPolly, "ress"); //$NON-NLS-1$
        this.createSignature(MSG.ressSigDesc, 
            MyPlugin.RESSOURCES_PERMISSION,
            new Parameter(MSG.ressSigExpression, Types.ANY));
        this.createSignature(MSG.ressSigDesc, 
            MyPlugin.RESSOURCES_PERMISSION,
            new Parameter(MSG.ressSigExpression, Types.ANY),
            new Parameter(MSG.ressSigExpression, Types.ANY));
        this.createSignature(MSG.ressSigDesc, 
            MyPlugin.RESSOURCES_PERMISSION,
            new Parameter(MSG.ressSigExpression, Types.ANY),
            new Parameter(MSG.ressSigExpression, Types.ANY),
            new Parameter(MSG.ressSigExpression, Types.ANY));
        this.setHelpText(MSG.ressHelp);
        this.setRegisteredOnly(true);
        this.rpgrabber = new ResourcePriceGrabber(REFRESH_THRESHOLD, myPolly);
    }

    
    
    @Override
    public void renewConstants(Map<String, Types> map) {
        final Map<String, Types> prices = this.rpgrabber.getPrices();
        map.putAll(prices);
        final List<Types> types = new ArrayList<Types>();
        for (final Entry<String, Types> e : prices.entrySet()) {
            types.add(new Types.StringType(e.getKey() + ":" + e.getValue().valueString( //$NON-NLS-1$
                this.getMyPolly().formatting())));
        }
        final Types.ListType lt = new Types.ListType(types);
        map.put("all", lt); //$NON-NLS-1$
        map.put("time", new Types.DateType(this.rpgrabber.getlastRefreshDate())); //$NON-NLS-1$
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        this.reply(channel, this.execute(signature));
        return false;
    }
    
    
    
    private String execute(Signature signature) {
        FormatManager fm = this.getMyPolly().formatting();
        if (this.match(signature, 0)) {
            Types t1 = signature.getValue(0);
            return t1.valueString(fm);
        } else if (this.match(signature, 1)) {
            Types t1 = signature.getValue(0);
            Types t2 = signature.getValue(1);
            return t1.valueString(fm) + " " + t2.valueString(fm); //$NON-NLS-1$
        } else if (this.match(signature, 2)) {
            Types t1 = signature.getValue(0);
            Types t2 = signature.getValue(1);
            Types t3 = signature.getValue(2);
            return t1.valueString(fm) + " " + t2.valueString(fm) + " " +  //$NON-NLS-1$ //$NON-NLS-2$
                    t3.valueString(fm);
        }
        // unreachable
        return ""; //$NON-NLS-1$
    }
}
