package core;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import entities.TopicEntity;


/**
 * 
 * @author Simon
 * @version 27.07.2011 3851c1b
 */
public class TopicManager extends AbstractDisposable {

    private final static long ONE_DAY = 86400000;
    
    private Timer topicTimer;
    private Map<String, TopicEntity> topics;
    private MyPolly myPolly;
    private PersistenceManager persistence;
    private TopicFormatter formatter;
    
    
    
    public TopicManager(MyPolly myPolly) {
        this.myPolly = myPolly;
        this.persistence = myPolly.persistence();
        this.formatter = new DefaultTopicFormatter();
        this.topics = new HashMap<String, TopicEntity>();
        
        this.topicTimer = new Timer("TOPICTIMER");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 24);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        this.topicTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TopicManager.this.dayChange();
            }
        }, c.getTime(), ONE_DAY);
    }
    
    
    
    public void loadAll() {
        try {
            this.persistence.readLock();
            List<TopicEntity> topics = this.persistence.findList(TopicEntity.class, 
                    "ALL_TOPICS");
            for (TopicEntity topic : topics) {
                this.topics.put(topic.getChannel(), topic);
            }
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    public void addTopicTask(TopicEntity entity) {
        if (this.topics.containsKey(entity.getChannel())) {
            return;
        }
        try {
            this.persistence.writeLock();
            this.persistence.startTransaction();
            this.persistence.persist(entity);
            this.persistence.commitTransaction();
            this.topics.put(entity.getChannel(), entity);
            this.myPolly.irc().setTopic(entity.getChannel(), 
                    this.formatter.formatTopic(entity));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    
    
    public List<TopicEntity> topicsForUser(String nickName) {
        try {
            this.persistence.readLock();
            return this.persistence.findList(TopicEntity.class, 
                    "TOPICS_FOR_USER", nickName);
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    public void deleteTopicTask(String channel) throws DatabaseException {
        TopicEntity entity = this.topics.get(channel);
        if (entity != null) {
            this.topics.remove(channel);
            try {
                this.persistence.writeLock();
                TopicEntity t = this.persistence.find(TopicEntity.class, entity.getId());
                this.persistence.startTransaction();
                this.persistence.remove(t);
                this.persistence.commitTransaction();
            } finally {
                this.persistence.writeUnlock();
            }
        }
    }

    
    
    public void dayChange() {
        List<TopicEntity> dues = new LinkedList<TopicEntity>();
        System.out.println("day change.");
        for (TopicEntity e : this.topics.values()) {
            System.out.println("topic change");
            if (e.isDue()) {
                this.myPolly.irc().setTopic(e.getChannel(), e.getAfter());
                dues.add(e);
            } else {
                this.myPolly.irc().setTopic(e.getChannel(), 
                        this.formatter.formatTopic(e));
            }
        }
        
        
        for (TopicEntity e : dues) {
            try {
                this.deleteTopicTask(e.getChannel());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }



    @Override
    protected void actualDispose() throws DisposingException {
        this.topicTimer.cancel();
        this.topics.clear();
        this.topicTimer = null;
        this.topics = null;
        this.persistence = null;
    }
}