package commands;

import java.util.HashMap;
import java.util.Map;

import polly.reminds.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class RemindSettingsCommand extends Command {

    private final static String REMIND_PATTERN = "remind_pattern";
    private final static String SLEEP_TIME = "sleep_time";
    
    private Map<String, Object> defaultValues;
    
    
    
    public RemindSettingsCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "remindset");
        this.createSignature("Setzt die Remindeinstellung mit dem angegebenen Namen " +
        		"auf den angegebenen Wert.",
        		new StringType(), new StringType());
        this.createSignature("Setzt die Remindeinstellung mit dem angegebenen Namen " +
            "auf den angegebenen Wert.",
            new StringType(), new NumberType());
        
        this.setRegisteredOnly();
        this.setHelpText("Befehl um individuelle Remind-Einstellungen zu ändern.");
        this.defaultValues = new HashMap<String, Object>();
        this.setDefaults();
    }
    
    
    
    private void setDefaults() {
        this.defaultValues.put(REMIND_PATTERN, MyPlugin.REMIND_FORMAT_VALUE);
        this.defaultValues.put(SLEEP_TIME, MyPlugin.SLEEP_DEFAULT_VALUE);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {

        String settingName = "";
        String settingValue = "";
        if (this.match(signature, 0)) {
            settingName = signature.getStringValue(0);
            settingValue = signature.getStringValue(1);
        } else if (this.match(signature, 1)) {
            settingName = signature.getStringValue(0);
            settingValue = Integer.toString((int) signature.getNumberValue(1));
        }
        
        if (this.applySetting(executer, settingName, settingValue)) {
            this.reply(channel, "Einstellung erfolgreich geändert.");
        } else {
            this.reply(channel, "Einstellung konnte nicht gespeichert werden.");
        }
        
        return false;
    }
    
    
    
    private boolean applySetting(User executer, String name, String value) {
        PersistenceManager persistence = this.getMyPolly().persistence();
        
        if (value.equalsIgnoreCase("%default%")) {
            value = (String) this.defaultValues.get(name);
        }
        
        String attributeName = "";
        
        if (name.equalsIgnoreCase(REMIND_PATTERN)) {
            attributeName = MyPlugin.REMIND_FORMAT_NAME;
        } else if (name.equalsIgnoreCase(SLEEP_TIME)) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return false;
            }
            attributeName = MyPlugin.SLEEP_TIME;
        } else {
            return false;
        }
        
        try {
            persistence.writeLock();
            persistence.startTransaction();
            executer.setAttribute(attributeName, value);
            persistence.commitTransaction();
        } catch (Exception e) {
            return false;
        } finally {
            persistence.writeUnlock();
        }
        
        
        return true;
    }

}
