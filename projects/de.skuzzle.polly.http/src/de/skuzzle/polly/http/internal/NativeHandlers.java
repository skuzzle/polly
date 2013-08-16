package de.skuzzle.polly.http.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.ParameterHandler;


class NativeHandlers {

    public final static ParameterHandler STRING = new ParameterHandler() {
        @Override
        public Object parse(String value) {
            return value;
        }
        
        @Override
        public boolean canHandle(Class<?> type, Class<?> typeVar) {
            return String.class.isAssignableFrom(type);
        }
    };
    
    
    
    public final static ParameterHandler INTEGER = new ParameterHandler() {
        
        @Override
        public Object parse(String value) throws HttpException {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new HttpException(e);
            }
        }
        
        @Override
        public boolean canHandle(Class<?> type, Class<?> typeVar) {
            return Integer.class.isAssignableFrom(type) || 
                int.class.isAssignableFrom(type);
        }
    };
    
    
    
    public final static ParameterHandler STRING_LIST = new ParameterHandler() {
        
        @Override
        public Object parse(String value) throws HttpException {
            if (value.equals("")) {
                return new ArrayList<String>();
            }
            return new ArrayList<String>(Arrays.asList(value.split(";")));
        }
        
        
        
        @Override
        public boolean canHandle(Class<?> type, Class<?> typeVar) {
            return List.class.isAssignableFrom(type) && typeVar != null && 
                String.class.isAssignableFrom(typeVar);
        }
    };
    
    
    
    public final static ParameterHandler INT_LIST = new ParameterHandler() {
        
        @Override
        public Object parse(String value) throws HttpException {
            if (value.equals("")) {
                return new ArrayList<Integer>();
            }
            final String[] parts = value.split(";");
            final List<Integer> result = new ArrayList<Integer>();
            for (final String p : parts) {
                try {
                    result.add(Integer.parseInt(p));
                } catch (NumberFormatException e) {
                    throw new HttpException(e);
                }
            }
            return result;
        }
        
        
        
        @Override
        public boolean canHandle(Class<?> type, Class<?> typeVar) {
            return List.class.isAssignableFrom(type) && typeVar != null && 
                Integer.class.isAssignableFrom(typeVar);
        }
    };
}
