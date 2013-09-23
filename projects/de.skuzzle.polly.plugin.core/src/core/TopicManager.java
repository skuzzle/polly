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
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.CommandException;
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
    private PersistenceManagerV2 persistence;
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
        try (final Read r = this.persistence.read()) {
            List<TopicEntity> topics = r.findList(TopicEntity.class, "ALL_TOPICS");
            for (TopicEntity topic : topics) {
                this.topics.put(topic.getChannel(), topic);
            }
        }
    }
    
    
    
    public void addTopicTask(final TopicEntity entity) throws CommandException {
        if (this.topics.containsKey(entity.getChannel())) {
            return;
        }
        try {
            this.persistence.writeAtomic(new Atomic() {
                @Override
                public void perform(Write write) {
                    write.single(entity);
                    topics.put(entity.getChannel(), entity);
                    myPolly.irc().setTopic(entity.getChannel(), formatter.formatTopic(entity));
                }
            });
        } catch (DatabaseException e) {
            throw new CommandException(e);
        }
    }
    
    
    
    public List<TopicEntity> topicsForUser(String nickName) {
        return this.persistence.atomic().findList(TopicEntity.class, 
                    "TOPICS_FOR_USER", new Param(nickName));
    }
    
    
    
    public void deleteTopicTask(String channel) throws DatabaseException {
        final TopicEntity entity = this.topics.get(channel);
        if (entity != null) {
            this.topics.remove(channel);
            this.persistence.writeAtomic(new Atomic() {
                
                @Override
                public void perform(Write write) {
                    final TopicEntity t = write.read().find(TopicEntity.class, 
                        entity.getId());
                    write.remove(t);
                }
            });
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