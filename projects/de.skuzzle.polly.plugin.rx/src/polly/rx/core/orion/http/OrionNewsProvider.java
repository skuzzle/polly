package polly.rx.core.orion.http;

import java.util.ArrayList;
import java.util.Collection;

import polly.rx.core.orion.FleetEvent;
import polly.rx.core.orion.FleetListener;
import polly.rx.core.orion.model.Fleet;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;

public class OrionNewsProvider implements HttpEventHandler, FleetListener {

    private final Collection<Fleet> orionFleets;



    public OrionNewsProvider() {
        this.orionFleets = new ArrayList<>();
    }



    @Override
    public void ownFleetsUpdated(FleetEvent e) {
        synchronized (this.orionFleets) {
            this.orionFleets.clear();
            this.orionFleets.addAll(e.getFleets());
        }
    }



    @Override
    public void fleetsUpdated(FleetEvent e) {}



    @Override
    public HttpAnswer handleHttpEvent(String registered, HttpEvent e,
            HttpEventHandler next) throws HttpException {
        return null;
    }

}
