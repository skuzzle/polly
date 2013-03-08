package de.skuzzle.polly.core.internal.persistence;

import java.io.PrintStream;
import java.util.ArrayList;

public class EntityList extends ArrayList<Class<?>>{

    private static final long serialVersionUID = 1L;

    
    
    public void toString(PrintStream s) {
        for (Class<?> clazz : this) {
            s.print("    <class>");
            s.print(clazz.getName());
            s.print("</class>\n");
        }
    }

}
