package de.skuzzle.polly.parsing.declarations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;



import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;
import de.skuzzle.polly.parsing.util.CopyTool;

public class Declarations {

    private LinkedList<Map<String, Declaration>> levels;



    public Declarations() {
        this.levels = new LinkedList<Map<String, Declaration>>();
        this.enter();
    }



    public void enter() {
        this.levels.addFirst(new HashMap<String, Declaration>());
    }



    public void leave() {
        this.levels.removeFirst();
    }



    public void add(Declaration declaration) {
        if (declaration.getType() == null) {
            throw new IllegalStateException(
                "Can not store Declaration with no type: " + declaration);
        }
        Map<String, Declaration> level = this.levels.getFirst();
        level.put(declaration.getName().getIdentifier(), declaration);
    }



    public Declaration tryResolve(IdentifierLiteral id, boolean hideLocals) {
        for (Map<String, Declaration> level : this.levels) {
            Declaration decl = level.get(id.getIdentifier());
            if (decl != null) {
                if (hideLocals && decl instanceof VarDeclaration && 
                        !((VarDeclaration)decl).isLocal()) {
                    return CopyTool.copyOf(decl);
                } else if (!hideLocals) {
                    return CopyTool.copyOf(decl);
                }
            }
        }

        return null;
    }
    
    
    
    public void remove(String name) {
        for (Map<String, Declaration> level : this.levels) {
            level.remove(name);
        }
    }
    
    
    
    public void store(File file) throws IOException {
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(this);
            output.flush();
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
    
    
    
    public static Declarations restore(File file) throws IOException {
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(file));
            Declarations result = (Declarations) input.readObject();
            return result;
        } catch (ClassNotFoundException e) {
            throw new IOException("invalid declaration file: " + file);
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
    
    
    
    @Override
    public Object clone() {
        Declarations result = new Declarations();
        result.levels = new LinkedList<Map<String,Declaration>>(this.levels);
        return result;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (Map<String, Declaration> level : this.levels) {
            result.append("Level " + i++);
            result.append("\n");
            for (Entry<String, Declaration> decl : level.entrySet()) {
                result.append("    ");
                result.append(decl.getKey());
                result.append(" = ");
                result.append(decl.getValue().toString());
                result.append("\n");
            }
        }
        return result.toString();
    }
}