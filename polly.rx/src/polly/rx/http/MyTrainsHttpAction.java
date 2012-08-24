package polly.rx.http;


import polly.rx.MyPlugin;
import polly.rx.core.TrainBillV2;
import polly.rx.core.TrainManagerV2;
import polly.rx.core.TrainSorter;
import polly.rx.core.TrainSorter.SortKey;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;

public class MyTrainsHttpAction extends HttpAction {

    private TrainManagerV2 trainManager;
    
    
    
    public MyTrainsHttpAction(MyPolly myPolly, TrainManagerV2 trainManager) {
        super("/MyTrains", myPolly);
        this.trainManager = trainManager;
        this.permissions.add(MyPlugin.MYTRAINS_PERMISSION);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e)
            throws HttpTemplateException {
        
        HttpTemplateContext c = new HttpTemplateContext("pages/mytrains.html");
        
        SortKey openSortKey = SortKey.parseSortKey(e.getProperty("openSortKey"));
        SortKey closedSortKey = SortKey.parseSortKey(e.getProperty("closedSortKey"));
        
        boolean openDesc = e.getProperty("openDesc") != null && 
                e.getProperty("openDesc").equals("true");
        boolean closedDesc = e.getProperty("closedDesc") != null && 
                e.getProperty("closedDesc").equals("true");
        
        String name = e.getSession().getUser().getName();
        TrainBillV2 allOpen = this.trainManager.getAllOpenTrains(name);
        TrainBillV2 allClosed = this.trainManager.getClosedTrains(name);
        
        TrainSorter.sort(allOpen.getTrains(), openSortKey, openDesc);
        TrainSorter.sort(allClosed.getTrains(), closedSortKey, closedDesc);
        
        c.put("openDesc", openDesc);
        c.put("closedDesc", closedDesc);
        c.put("openSortKey", openSortKey);
        c.put("closedSortKey", closedSortKey);
        c.put("trainManager", this.trainManager);
        c.put("allOpen", allOpen);
        c.put("allClosed", allClosed);
        
        return c;
    }
}
