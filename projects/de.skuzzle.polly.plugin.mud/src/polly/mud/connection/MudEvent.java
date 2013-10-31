package polly.mud.connection;

import de.skuzzle.polly.tools.events.Event;


public class MudEvent extends Event<MudTCPConnection> {

    public MudEvent(MudTCPConnection source) {
        super(source);
    }
}
