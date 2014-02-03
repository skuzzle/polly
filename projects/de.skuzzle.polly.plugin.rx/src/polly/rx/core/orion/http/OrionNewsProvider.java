package polly.rx.core.orion.http;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.gson.Gson;

import polly.rx.core.orion.FleetEvent;
import polly.rx.core.orion.FleetListener;
import polly.rx.core.orion.FleetTracker;
import polly.rx.core.orion.PortalEvent;
import polly.rx.core.orion.PortalListener;
import polly.rx.core.orion.PortalUpdater;
import polly.rx.core.orion.http.NewsEntry.NewsType;
import polly.rx.core.orion.model.Fleet;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.json.OrionJsonAdapter;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;

public class OrionNewsProvider implements HttpEventHandler, FleetListener, PortalListener {

    public final static String NEWS_URL = "/api/orion/json/news"; //$NON-NLS-1$
            
    private final static int MAX_NEWS = 20;
    private final Deque<NewsEntry> entries;

    

    
    public OrionNewsProvider(FleetTracker fleetTracker, PortalUpdater portalUpdater) {
        this.entries = new ArrayDeque<>();
        fleetTracker.addFleetListener(this);
        portalUpdater.addPortalListener(this);
    }
    
    
    
    
    
    private void addNews(NewsEntry e) {
        synchronized (this.entries) {
            if (this.entries.contains(e)) {
                return;
            }
            if (this.entries.size() == MAX_NEWS) {
                this.entries.removeLast();
            }
            this.entries.addFirst(e);
        }
    }



    @Override
    public void ownFleetsUpdated(FleetEvent e) {}



    @Override
    public void fleetsUpdated(FleetEvent e) {
        for (final Fleet fleet : e.getFleets()) {
            this.addNews(new NewsEntry(e.getReporter(), 
                    NewsType.FLEET_SPOTTED,
                    fleet,
                    fleet.getDate()));
        }
    }



    @Override
    public HttpAnswer handleHttpEvent(String registered, HttpEvent e,
            HttpEventHandler next) throws HttpException {
        
        synchronized (this.entries) {
            final NewsEntry[] entryArray = new NewsEntry[this.entries.size()];
            this.entries.toArray(entryArray);
            
            return HttpAnswers.newStringAnswer(OrionJsonAdapter.GSON.toJson(entryArray));
        }
    }



    @Override
    public void portalsAdded(PortalEvent e) {
        for (final Portal p : e.getPortals()) {
            this.addNews(new NewsEntry(e.getReporter(), 
                    NewsType.PORTAL_ADDED,
                    p, 
                    p.getDate()));
        }
    }



    @Override
    public void portalsMoved(PortalEvent e) {
        for (final Portal p : e.getPortals()) {
            this.addNews(new NewsEntry(e.getReporter(), 
                    NewsType.PORTAL_MOVED,
                    p, 
                    p.getDate()));
        }
    }



    @Override
    public void portalsRemoved(PortalEvent e) {
        for (final Portal p : e.getPortals()) {
            this.addNews(new NewsEntry(e.getReporter(), 
                    NewsType.PORTAL_REMOVED,
                    p,
                    p.getDate()));
        }
    }

}
