package de.skuzzle.polly.tools.streams;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public final class CopyTool {

    /**
     * This method creates a deep copy of the provided object using serialization. Thus
     * objects to copy must implement {@link Serializable}.
     * 
     * @param root Root object to copy. All non-transient fields of this class and all
     *          child classes will be copied.
     * @return A new instance which is a deep copy of the provided object.
     */
    @SuppressWarnings("unchecked")
    public final static <T> T copyOf(T root) {
        final FastByteArrayOutputStream buffer = new FastByteArrayOutputStream();
        try {
            new ObjectOutputStream(buffer).writeObject(root);
            return (T) new ObjectInputStream(
                    new FastByteArrayInputStream(buffer)).readObject();
        } catch (Exception e) {
            throw new RuntimeException("clone of object " + root + " failed", e);
        }
    }
    
    
    
    private CopyTool() {}
}