package de.skuzzle.polly.core.internal.paste;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import de.skuzzle.polly.sdk.exceptions.PasteException;
import de.skuzzle.polly.sdk.paste.PasteService;
import de.skuzzle.polly.sdk.paste.PasteServiceManager;


public class PasteServiceManagerImpl implements PasteServiceManager {
    
    private Map<String, PasteService> pasteServices;
    private Queue<PasteService> pasteQueue;
    
    public PasteServiceManagerImpl() {
        this.pasteServices = new HashMap<String, PasteService>();
        this.pasteQueue = new LinkedList<PasteService>();
    }
    

    
    @Override
    public void addService(PasteService service) throws PasteException {
        synchronized (this.pasteServices) {
            this.pasteServices.put(service.getName(), service);            
        }
        
        synchronized (this.pasteQueue) {
            this.pasteQueue.add(service);            
        }
    }

    
    
    @Override
    public PasteService getService(String name) throws PasteException {
        PasteService result = null;
        synchronized (this.pasteServices) {
            result = this.pasteServices.get(name);           
        }
        if (result == null) {
            throw new PasteException("no PasteService for the name '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return result;
    }

    
    
    @Override
    public PasteService getRandomService() {
        synchronized (this.pasteQueue) {
            if (this.pasteQueue.isEmpty()) {
                return null;
            }
            PasteService result = this.pasteQueue.poll();
            this.pasteQueue.offer(result);
            return result;
        }
    }

    
    
    @Override
    public Collection<PasteService> getAllServices() {
        return Collections.unmodifiableCollection(this.pasteServices.values());
    }

}
