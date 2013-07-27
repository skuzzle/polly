package de.skuzzle.polly.core.internal.persistence;

import de.skuzzle.polly.sdk.EntityConverter;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;


public class EntityConverterManagerImpl {

    private final static Logger logger = Logger
        .getLogger(EntityConverterManagerImpl.class.getName());
    
    
    private List<EntityConverter> converters;
    private PersistenceManager persistence;
    
    
    
    public EntityConverterManagerImpl(PersistenceManager persistence) {
        this.persistence = persistence;
        this.converters = new LinkedList<EntityConverter>();
    }
    
    
    
    public void convertAll() throws DatabaseException {
        // CONSIDER: One transaction for each converter?
        this.persistence.atomicWriteOperation(new WriteAction() {
            @Override
            public void performUpdate(PersistenceManager persistence) {
                for (EntityConverter ec : converters) {
                    try {
                        logger.info("Running Entity Converter: " + ec);
                        runConverter(ec);
                    } catch (Exception e) {
                        logger.error("Error while converting entities", e);
                    }
                }
            }
        });
    }
    
    
    
    private void runConverter(EntityConverter ec) {
        logger.trace("Retrieving list of old entities...");
        List<Object> olds = ec.getOldEntities(this.persistence);
        List<Object> converted = new ArrayList<Object>(olds.size());
        
        logger.trace("Converting " + olds.size() + " entities...");
        for (Object old : olds) {
            converted.add(ec.convertEntity(old));
        }
        
        logger.trace("Persisting list of converted entities...");
        this.persistence.persistList(converted);
        
        logger.trace("Deleting old entities (optional operation, may have no effect)...");
        ec.deleteOldEntities(olds, this.persistence);
    }



    public void addConverter(EntityConverter ec) {
        logger.info("Registered entity converter:" + ec);
        this.converters.add(ec);
    }
}
