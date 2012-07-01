package de.skuzzle.polly.tools.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Permutator {
    
    public final static int MAX_FACULTY_SIZE = 10;

    
    public static int faculty(int n) {
        if (n < 2) {
            return 1;
        }
        return n * faculty(n - 1);
    }
    
    
    
    public static <T> List<T[]> permute(T[] arr) {
        if (arr.length > MAX_FACULTY_SIZE) {
            throw new IllegalArgumentException("input arry too big");
        }
        List<T[]> results = new ArrayList<T[]>(faculty(arr.length));
        permute(arr, 0, results);
        return results;
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    public static <T> List<T[]> permute(List<T> list) {
        return permute((T[]) list.toArray());
    }
   
    
    
    private static <T> void permute(T[] arr, int i, List<T[]> results) {
        if (i == arr.length - 1) {
            results.add(Arrays.copyOf(arr, arr.length));
            return;
        }
        
        for (int j = i; j < arr.length; ++j) {
            swap(arr, i, j);
            permute(arr, i + 1, results);
            swap(arr, i, j);
        }
        
    }
    
    
    
    private static <T> void swap(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}