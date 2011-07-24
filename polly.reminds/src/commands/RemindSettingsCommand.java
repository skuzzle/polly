package commands;

import java.util.HashMap;
import java.util.Map;

import polly.reminds.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class RemindSettingsCommand extends Command {

    private final static String REMIND_PATTERN = "remind_pattern";
    
    private Map<String, Object> defaultValues;
    
    
    
    public RemindSettingsCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "remindset");
        this.createSignature("Setzt die Remindeinstellung mit dem angegebenen Namen " +
        		"auf den angegebenen Wert.",
        		new StringType(), new StringType());
        
        this.setRegisteredOnly();
        this.setHelpText("Befehl um individuelle Remind-Einstellungen zu ändern.");
        this.defaultValues = new HashMap<String, Object>();
        this.setDefaults();
    }
    
    
    
    private void setDefaults() {
        this.defaultValues.put(REMIND_PATTERN, MyPlugin.REMIND_FORMAT_VALUE);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {

        if (this.match(signature, 0)) {
            String settingName = signature.getStringValue(0);
            String settingValue = signature.getStringValue(1);
            if (this.applySetting(executer, settingName, settingValue)) {
                this.reply(channel, "Einstellung erfolgreich geändert.");
            }
        }
        return false;
    }
    
    
    
    private boolean applySetting(User executer, String name, String value) {
        PersistenceManager persistence = this.getMyPolly().persistence();
        
        if (value.equalsIgnoreCase("%default%")) {
            value = (String) this.defaultValues.get(name);
        }
        
        if (name.equalsIgnoreCase("remind_pattern")) {
            try {
                persistence.writeLock();
                persistence.startTransaction();
                executer.setAttribute(MyPlugin.REMIND_FORMAT_NAME, value);
                persistence.commitTransaction();
            } catch (Exception e) {
                return false;
            } finally {
                persistence.writeUnlock();
            }
        } else {
            return false;
        }
        
        return true;
    }

}
