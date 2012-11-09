package polly.core;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.eventlistener.DayChangedListener;
import de.skuzzle.polly.sdk.exceptions.EMailException;
import de.skuzzle.polly.sdk.time.Time;


public class DayChangeTester implements DayChangedListener {

    private final MyPolly myPolly;
    
    
    public DayChangeTester(MyPolly myPolly) {
        this.myPolly = myPolly;
        Time.addDayChangeListener(this);
    }


    @Override
    public void dayChanged(long currentTime) {
        try {
            this.myPolly.mails().sendMail("simon.taddiken@gmail.com", 
                "Day has changed", "Am i doing it right?");
        } catch (EMailException e) {
            e.printStackTrace();
        }
    }
}
