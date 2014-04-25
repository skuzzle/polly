package polly.annoyingPeople;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import polly.annoyingPeople.entities.AnnoyingPerson;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.JoinPartListener;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;

public class PeopleAnnoyer extends AbstractDisposable 
        implements PersonListener, JoinPartListener {

    public final static long ANNOY_RATE = Milliseconds.fromSeconds(15);

    private final static String[] RESS_NAMES = MSG.ressNames.split(";"); //$NON-NLS-1$
    private final static String[] AKTIEN_NAMES = MSG.aktienNames.split(";"); //$NON-NLS-1$
    private final static String[] QUAD_NAMES = MSG.quadNames.split(";"); //$NON-NLS-1$
    private final static Random RANDOM = new Random();


    private final static List<Function<String, String>> MESSAGES = new ArrayList<>();
    static {
        MESSAGES.add(PeopleAnnoyer::askForRessPrice);
        MESSAGES.add(PeopleAnnoyer::askForCourse);
        MESSAGES.add(PeopleAnnoyer::askForKonstru);
        MESSAGES.add(PeopleAnnoyer::askForDirection);
        MESSAGES.add(PeopleAnnoyer::askIfDown);
        MESSAGES.add(PeopleAnnoyer::askForCode);
    }
    

    private static String randomOf(String[] a) {
        return a[RANDOM.nextInt(a.length)];
    }



    private static String askForRessPrice(String name) {
        final String rndRess = randomOf(RESS_NAMES);
        final String clause = randomOf(MSG.askForRessPrice.split(";")); //$NON-NLS-1$
        return MSG.bind(clause, name, rndRess);
    }



    private static String askForCourse(String name) {
        final String rndAktie = randomOf(AKTIEN_NAMES);
        final String clause = randomOf(MSG.askForCourse.split(";")); //$NON-NLS-1$
        return MSG.bind(clause, name, rndAktie);
    }
    
    
    
    private static String askForKonstru(String name) {
        final int rndLevel = 20 + RANDOM.nextInt(30);
        final String clause = randomOf(MSG.askForKonstru.split(";")); //$NON-NLS-1$
        return MSG.bind(clause, name, "" + rndLevel); //$NON-NLS-1$
    }
    
    
    
    private static String askForDirection(String name) {
        final String rndQuad = randomOf(QUAD_NAMES);
        final String clause = randomOf(MSG.askForDirection.split(";")); //$NON-NLS-1$
        return MSG.bind(clause, name, rndQuad);
    }
    
    
    
    private static String askIfDown(String name) {
        final String clause = randomOf(MSG.askIfDown.split(";")); //$NON-NLS-1$
        return MSG.bind(clause, name);
    }
    
    
    
    private static String askForCode(String name) {
        final String clause = randomOf(MSG.askForCode.split(";")); //$NON-NLS-1$
        return MSG.bind(clause, name);
    }
    
    
    
    private static String randomAnnoyingMessage(String name) {
        final Function<String, String> func = MESSAGES.get(
                RANDOM.nextInt(MESSAGES.size()));
        return func.apply(name);
    }
    
    

    private class AnnoyTask implements Runnable {

        @Override
        public void run() {
            synchronized (annoyNames) {
                if (annoyNames.isEmpty()) {
                    return;
                }
                final AnnoyingPerson rndPerson = annoyNames.get(
                        RANDOM.nextInt(annoyNames.size()));
                
                if (true || RANDOM.nextBoolean()) {
                    final String rndMsg = randomAnnoyingMessage(rndPerson.getName());
                    irc.sendMessage(rndPerson.getChannel(), rndMsg);
                }
            }
        }
    }
    
    

    private final ScheduledExecutorService annoyService;
    private final PersonManager personManager;
    private final IrcManager irc;
    private final List<AnnoyingPerson> annoyNames;



    public PeopleAnnoyer(PersonManager personManager, IrcManager irc) {
        this.annoyService = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder("ANNOY_SERVICE")); //$NON-NLS-1$
        this.annoyService.scheduleAtFixedRate(new AnnoyTask(), ANNOY_RATE, ANNOY_RATE, 
                TimeUnit.MILLISECONDS);
        this.personManager = personManager;
        this.irc = irc;
        this.annoyNames = new ArrayList<>();
    }



    @Override
    public void channelJoined(ChannelEvent e) {
        final AnnoyingPerson ap = this.personManager.getAnnoyingPerson(e.getUser()
                .getNickName(), e.getChannel());

        if (ap != null) {
            synchronized (this.annoyNames) {
                this.annoyNames.add(ap);
            }
        }
    }



    @Override
    public void channelParted(ChannelEvent e) {
        final AnnoyingPerson ap = this.personManager.getAnnoyingPerson(e.getUser()
                .getNickName(), e.getChannel());

        if (ap != null) {
            synchronized (this.annoyNames) {
                this.annoyNames.remove(ap);
            }
        }
    }



    @Override
    public void personAdded(AnnoyingPersonEvent e) {
        if (this.irc.isOnChannel(e.getPerson().getChannel(), e.getPerson().getName())) {
            synchronized (this.annoyNames) {
                this.annoyNames.add(e.getPerson());
            }
        }
    }



    @Override
    public void personRemoved(AnnoyingPersonEvent e) {
        synchronized (this.annoyNames) {
            this.annoyNames.remove(e.getPerson());
        }
    }



    @Override
    protected void actualDispose() throws DisposingException {
        this.annoyService.shutdownNow();
    }

}
