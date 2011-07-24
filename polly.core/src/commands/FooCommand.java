package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.AnyType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class FooCommand extends Command {

    public FooCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "foo");
        this.createSignature("Foo-Befehl mit einem Parameter.", new AnyType());
        this.createSignature("Foo-Befehl mit zwei Parametern.", 
                new AnyType(), new AnyType());
        this.createSignature("Foo-Befehl mit drei Parametern", new AnyType(), 
                new AnyType(), new AnyType());
        this.createSignature("Foo-Befehl mit 0 Parametern.");
        this.setHelpText("Dieser Befehl nimmt jede art von Parametern entgegen, " +
        		"wertet ihn aus und gibt das Ergebnis zurück.");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        FormatManager fm = this.getMyPolly().formatting();
        
        if (this.match(signature, 0)) {
            Types t1 = signature.getValue(0);
            this.reply(channel, t1.valueString(fm));
        } else if (this.match(signature, 1)) {
            Types t1 = signature.getValue(0);
            Types t2 = signature.getValue(1);
            this.reply(channel, t1.valueString(fm) + " " + t2.valueString(fm));
        } else if (this.match(signature, 2)) {
            Types t1 = signature.getValue(0);
            Types t2 = signature.getValue(1);
            Types t3 = signature.getValue(2);
            this.reply(channel, t1.valueString(fm) + " " + t2.valueString(fm) + " " + 
                    t3.valueString(fm));
        }
        return false;
    }

}
