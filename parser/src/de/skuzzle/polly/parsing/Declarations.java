package de.skuzzle.polly.parsing;

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

import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.FunctionDefinition;
import de.skuzzle.polly.parsing.tree.IdentifierLiteral;
import de.skuzzle.polly.parsing.tree.ResolveableIdentifierLiteral;
import de.skuzzle.polly.parsing.tree.TypeExpression;




public class Declarations {
    
    public final static Declarations GLOBAL;
    public final static Declarations RESERVED;
    
    static {
        GLOBAL = new Declarations();
        RESERVED = new Declarations();
        
        try {
            RESERVED.add(new TypeExpression(Type.STRING));
            RESERVED.add(new TypeExpression(Type.NUMBER));
            RESERVED.add(new TypeExpression(Type.BOOLEAN));
            RESERVED.add(new TypeExpression(Type.DATE));
            RESERVED.add(new TypeExpression(Type.ANY));
            RESERVED.add(new TypeExpression(Type.LIST));
            RESERVED.add(new TypeExpression(Type.CHANNEL));
            RESERVED.add(new TypeExpression(Type.USER));
            RESERVED.add(new TypeExpression(Type.TIMESPAN));
            
            /*RESERVED.add(new IdentifierLiteral("pi"), 
                    new NumberLiteral(Math.PI, Position.EMPTY));
            RESERVED.add(new IdentifierLiteral("e"), 
                    new NumberLiteral(Math.E, Position.EMPTY));
            RESERVED.add(new DefaultMathFunctions("sin"));
            RESERVED.add(new DefaultMathFunctions("cos"));
            RESERVED.add(new DefaultMathFunctions("tan"));
            RESERVED.add(new DefaultMathFunctions("abs"));
            RESERVED.add(new DefaultMathFunctions("sqrt"));
            RESERVED.add(new DefaultMathFunctions("ceil"));
            RESERVED.add(new DefaultMathFunctions("floor"));
            RESERVED.add(new DefaultMathFunctions("log"));
            RESERVED.add(new DefaultMathFunctions("round"));
            RESERVED.add(new RandomFunction());
            RESERVED.add(new ContainsFunction());
            RESERVED.add(new ListLengthFunction());*/
        } catch (ParseException ignore) {
            // this cannot happen
            ignore.printStackTrace();
        }
    }
    
    
    
    private LinkedList<Map<String, Expression>> levels;
    
    
    
    public Declarations() {
        this.levels = new LinkedList<Map<String,Expression>>();
        this.enter();
    }
    
    
    
    public void enter() {
        this.levels.addFirst(new HashMap<String, Expression>());
    }
    
    
    
    public void leave() {
        this.levels.removeFirst();
    }

    
    
    public Expression resolve(ResolveableIdentifierLiteral name) throws ParseException {
        Expression result = null;
        // search in private declarations

        for (Map<String, Expression> level : this.levels) {
            result = resolve(name, level);
            if (result != null) {
                return result;
            }
        }
        
        
        if (this != GLOBAL) {
            // search in public declarations
            for (Map<String, Expression> level : GLOBAL.levels) {
                result = resolve(name, level);
                if (result != null) {
                    return result;
                }
            }
        }
        
        throw new ParseException("Unbekannter Bezeichner '" + name + "'", 
                name.getPosition());
    }
    
    
    
    private Expression resolve(ResolveableIdentifierLiteral name, 
                Map<String, Expression> level) {
        Expression result = level.get(name.getIdentifier());
        if (result != null) {
            result.setPosition(name.getPosition());
            return (Expression) result.clone();
        }
        return null;
    }

    
    
    public void add(IdentifierLiteral identifier, 
            Expression expression) throws ParseException {
        this.add(identifier, expression, false);
    }
    
    
    
    public void add(IdentifierLiteral identifier, 
        Expression expression, boolean isPublic) throws ParseException {
        if (RESERVED.getDeclarations().containsKey(identifier.getIdentifier())) {
            throw new ParseException("'" + identifier.getIdentifier() + 
                    "' ist ein reservierter Bezeichner", identifier.getPosition());
        }
        Map<String, Expression> d = isPublic 
                ? GLOBAL.getDeclarations() : this.levels.getFirst();
    
        d.put(identifier.getIdentifier(), expression);
    }
    
    
    
    public void add(FunctionDefinition function) throws ParseException {
        this.add(function, false);
    }
    
    
    public void add(FunctionDefinition function, boolean isPublic) throws ParseException {
        this.add(function.getName(), function, isPublic);
    }
    
    
    
    public void add(TypeExpression e) throws ParseException {
        this.add(e.getType().getTypeName(), e);
    }
    
    
    
    public Map<String, Expression> getDeclarations() {
        return this.levels.getFirst();
    }
    
    
    
    public void store(File location) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(location));
        try {
            out.writeObject(this.levels);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        int i = 0;
        for (Map<String, Expression> level : this.levels) {
            b.append("Level: " + i++ + "\n");
            for (Entry<String, Expression> e : level.entrySet()) {
                b.append("    ");
                b.append(e.getKey());
                b.append(" := ");
                b.append(e.getValue().toString());
                b.append("\n");
            }
        }
        return b.toString();
    }
    
    
    
    @SuppressWarnings("unchecked")
    public static Declarations restore(File location) 
            throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(location));
        LinkedList<Map<String, Expression>> list = null;
        try {
            list = (LinkedList<Map<String, Expression>>) in.readObject();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        Declarations d = new Declarations();
        d.levels = list;
        return d;
    }
}