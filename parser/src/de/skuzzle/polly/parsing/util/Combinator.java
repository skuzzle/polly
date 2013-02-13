package de.skuzzle.polly.parsing.util;

import java.util.ArrayList;
import java.util.List;



public final class Combinator {

    public static interface CombinationCallBack<T, S> {
        public List<S> getSubList(T outer);
        
        public void onNewCombination(List<S> combination);
    }
    
    
    
    public final static <T, S> void combine(List<T> elements, 
            CombinationCallBack<T, S> ccb) {
        
        boolean allChecked = false;
        final int[] indizes = new int[elements.size()];
        final boolean done[] = new boolean[elements.size()];
        
        // combine all possible parameter types
        while (!allChecked) {
            allChecked = true;
            
            final List<S> combination = new ArrayList<S>();
            int i = 0;
            for (final T outer : elements) {
                List<S> subList = ccb.getSubList(outer);
                
                combination.add(subList.get(indizes[i]));
                
                done[i] = (indizes[i] + 1) == subList.size();
                allChecked &= done[i];
                indizes[i] = (indizes[i] + 1) % subList.size();
                ++i;
            }
            
            ccb.onNewCombination(combination);
        }
    }
    
    
    
    private Combinator() {}
}