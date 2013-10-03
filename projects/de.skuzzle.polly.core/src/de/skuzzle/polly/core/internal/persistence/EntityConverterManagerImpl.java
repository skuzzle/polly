package de.skuzzle.polly.core.internal.persistence;

import de.skuzzle.polly.sdk.EntityConverter;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;


public class EntityConverterManagerImpl {

    private final static Logger logger = Logger
        .getLogger(EntityConverterManagerImpl.class.getName());
    
    
    private List<EntityConverter> converters;
    private PersistenceManagerV2 persistence;
    
    
    
    public EntityConverterManagerImpl(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
        this.converters = new LinkedList<EntityConverter>();
    }
    
    
    
    public void convertAll() throws DatabaseException {
        // CONSIDER: One transaction for each converter?
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                for (EntityConverter ec : converters) {
                    try {
                        logger.info("Running Entity Converter: " + ec);
                        runConverter(ec, write);
                    } catch (Exception e) {
                        logger.error("Error while converting entities", e);
                    }
                }
            }
        });
    }
    
    
    
    private void runConverter(EntityConverter ec, Write write) {
        logger.trace("Retrieving list of old entities...");
        List<Object> olds = ec.getOldEntities(write.read());
        List<Object> converted = new ArrayList<Object>(olds.size());
        
        logger.trace("Converting " + olds.size() + " entities...");
        for (Object old : olds) {
            converted.add(ec.convertEntity(old));
        }
        
        logger.trace("Persisting list of converted entities...");
        write.all(converted);
        
        logger.trace("Deleting old entities (optional operation, may have no effect)...");
        ec.deleteOldEntities(olds, write);
    }



    public void addConverter(EntityConverter ec) {
        logger.info("Registered entity converter:" + ec);
        this.converters.add(ec);
    }
}
