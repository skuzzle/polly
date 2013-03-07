package polly.util;

import de.skuzzle.polly.sdk.time.SystemTimeProvider;


public class MillisecondStopwatch extends TimeProviderStopWatch {

    public MillisecondStopwatch() {
        super(new SystemTimeProvider());
    }

}
