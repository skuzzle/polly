package core.pasteservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


public class PasteServiceManager {

    private Map<String, PasteService> services;
    private Queue<PasteService> pasteQueue;
    private String defaultService;
    
    private final static Random RANDOM = new Random(System.currentTimeMillis());
    
    
    
    public PasteServiceManager(String defaultService) {
        this.services = new HashMap<String, PasteService>();
        this.pasteQueue = new LinkedList<PasteService>();
        this.defaultService = defaultService;
        
        this.addService(new NoPastePasteService());
        this.addService(new PasteBinPasteService());
        this.addService(new GBPasteService());
        this.addService(new PHCNPasteService());
    }
    
    
    
    private void addService(PasteService service) {
        this.services.put(service.getName(), service);
        this.pasteQueue.add(service);
    }
    
    
    
    public PasteService nextService() {
        synchronized (this.pasteQueue) {
            PasteService next = this.pasteQueue.poll();
            this.pasteQueue.add(next);
            return next;
        }
    }
    
    
    public PasteService getRandomService() {
        int r = RANDOM.nextInt(this.services.size());
        
        ArrayList<PasteService> rndList = new ArrayList<PasteService>(
                this.services.values());
        return rndList.get(r);
    }
    
    
    
    public PasteService getDefaultService() {
        return this.services.get(this.defaultService);
    }
}