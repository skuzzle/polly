package polly.mud.connection;

import de.skuzzle.jeve.Event;


public class MudEvent extends Event<MudTCPConnection> {

    public MudEvent(MudTCPConnection source) {
        super(source);
    }
}
