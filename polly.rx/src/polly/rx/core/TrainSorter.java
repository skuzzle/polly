package polly.rx.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import polly.rx.entities.TrainEntityV2;


public class TrainSorter {
    
    
    private final static Comparator<TrainEntityV2> BY_TRAINER = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return Integer.compare(te1.getTrainerId(), te2.getTrainerId());
        }
    };
    
    
    
    private final static Comparator<TrainEntityV2> BY_USER = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return te1.getForUser().compareTo(te2.getForUser());
        }
    };
    
    
    
    private final static Comparator<TrainEntityV2> BY_TYPE = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return te1.getType().compareTo(te2.getType());
        }
    };
    
    
    
    private final static Comparator<TrainEntityV2> BY_COSTS = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return Integer.compare(te1.getCosts(), te2.getCosts());
        }
    };
    
    
    
    private final static Comparator<TrainEntityV2> BY_FACTOR = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return Double.compare(te1.getFactor(), te2.getFactor());
        }
    };
    
    
    
    private final static Comparator<TrainEntityV2> BY_WEiGHTED = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return Double.compare(te1.getFactor() * te1.getCosts(), 
                    te2.getFactor() * te2.getCosts());
        }
    };
    
    
    
    private final static Comparator<TrainEntityV2> BY_START = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return te1.getTrainStart().compareTo(te2.getTrainStart());
        }
    };
    
    
    
    private final static Comparator<TrainEntityV2> BY_FINISHED = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return te1.getTrainFinished().compareTo(te2.getTrainFinished());
        }
    };
    
    
    
    private final static Comparator<TrainEntityV2> BY_DURATION = 
            new Comparator<TrainEntityV2>() {
        @Override
        public int compare(TrainEntityV2 te1, TrainEntityV2 te2) {
            return Long.compare(te1.getDuration(), te2.getDuration());
        }
    };
    
    
    
    
    private static class ReversingComparator implements Comparator<TrainEntityV2> {

        private Comparator<TrainEntityV2> comp;
        
        public ReversingComparator(Comparator<TrainEntityV2> comp) {
            this.comp = comp;
        }
        
        
        
        @Override
        public int compare(TrainEntityV2 o1, TrainEntityV2 o2) {
            return this.comp.compare(o2, o1);
        }
        
    }
    
    

    public static enum SortKey {
        BY_TRAINER(TrainSorter.BY_TRAINER), 
        BY_USER(TrainSorter.BY_USER), 
        BY_TYPE(TrainSorter.BY_TYPE), 
        BY_COSTS(TrainSorter.BY_COSTS),
        BY_WEIGHTED(TrainSorter.BY_WEiGHTED), 
        BY_FACTOR(TrainSorter.BY_FACTOR), 
        BY_START(TrainSorter.BY_START), 
        BY_FINISHED(TrainSorter.BY_FINISHED), 
        BY_DURATION(TrainSorter.BY_DURATION),
        NONE(null);
        
        private Comparator<TrainEntityV2> comp;
        
        
        public static SortKey parseSortKey(String key) {
            try {
                return SortKey.valueOf(key);
            } catch (Exception e) {
                return SortKey.NONE;
            }
        }
        
        private SortKey(Comparator<TrainEntityV2> comp) {
            this.comp = comp;
        }
        
        
        
        public Comparator<TrainEntityV2> getComparator() {
            return this.comp;
        }
    }
    
    
    
    public static void sort(List<TrainEntityV2> list, SortKey key, boolean desc) {
        if (key != SortKey.NONE) {
            Comparator<TrainEntityV2> c = desc 
                    ? new ReversingComparator(key.getComparator()) 
                    : key.getComparator();
            Collections.sort(list, c);
        }
    }
}