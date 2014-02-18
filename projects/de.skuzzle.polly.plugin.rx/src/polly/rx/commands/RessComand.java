package polly.rx.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.ResourcePriceProvider;
import polly.rx.core.orion.model.Production;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class RessComand extends Command {
    
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
    }

    
    
    @Override
    public void renewConstants(Map<String, Types> map) {
        final ResourcePriceProvider rpp = Orion.INSTANCE.getPriceProvider();
        final List<? extends Production> prices = rpp.getAllPrices();
            
        
        final List<Types> types = new ArrayList<Types>();
        for (final Production p : prices) {
            final NumberType rate = new NumberType(p.getRate());
            final String ress = p.getRess().toString().toLowerCase();
            map.put(ress, rate);
            types.add(new StringType(ress + ":" +  //$NON-NLS-1$
                    rate.valueString(this.getMyPolly().formatting())));
        }
        final Types.ListType lt = new Types.ListType(types);
        map.put("all", lt); //$NON-NLS-1$
        map.put("time", new Types.DateType(rpp.getRefreshTime())); //$NON-NLS-1$
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
