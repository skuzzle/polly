package de.skuzzle.polly.parsing;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.parsing.tree.IdentifierLiteral;
import de.skuzzle.polly.parsing.tree.NumberLiteral;
import de.skuzzle.polly.parsing.tree.ResolveableIdentifierLiteral;
import de.skuzzle.polly.parsing.tree.operators.ContainsFunction;
import de.skuzzle.polly.parsing.tree.operators.DefaultMathFunctions;
import de.skuzzle.polly.parsing.tree.operators.ListLengthFunction;
import de.skuzzle.polly.parsing.tree.operators.RandomFunction;


public class Namespaces {
    
    private final static Declarations MATH;
    static {
        MATH = new Declarations();
        try {            
            MATH.add(new IdentifierLiteral("pi"), 
                    new NumberLiteral(Math.PI, Position.EMPTY));
            MATH.add(new IdentifierLiteral("e"), 
                    new NumberLiteral(Math.E, Position.EMPTY));
            MATH.add(new IdentifierLiteral("tau"), 
                new NumberLiteral(2*Math.PI, Position.EMPTY));
            MATH.add(new DefaultMathFunctions("sin"));
            MATH.add(new DefaultMathFunctions("cos"));
            MATH.add(new DefaultMathFunctions("tan"));
            MATH.add(new DefaultMathFunctions("abs"));
            MATH.add(new DefaultMathFunctions("sqrt"));
            MATH.add(new DefaultMathFunctions("ceil"));
            MATH.add(new DefaultMathFunctions("floor"));
            MATH.add(new DefaultMathFunctions("log"));
            MATH.add(new DefaultMathFunctions("round"));
            MATH.add(new RandomFunction());
            MATH.add(new ContainsFunction());
            MATH.add(new ListLengthFunction());
        } catch (ParseException ignore) {}
    }

    private Map<String, Declarations> namespaces;
    
    
    
    public Namespaces() {
        this.namespaces = new HashMap<String, Declarations>();
        this.namespaces.put("Math", MATH);
    }
    
    
    
    public void create(String name, Declarations d) {
        if (!this.namespaces.containsKey(name)) {
            this.namespaces.put(name, d);
        }
    }
    
    
    
    public void create(String name) {
        this.create(name, new Declarations());
    }
    
    
    
    public Declarations get(String name, Position position) throws ParseException {
        Declarations space = this.namespaces.get(name);
        
        if (space == null) {
            throw new ParseException("Unbekannter Namespace '" + 
                name + "'", position);
        }
        
        return space;
    }
    
    
    
    public Declarations get(String name) throws ParseException {
        return this.get(name, Position.EMPTY);
    }
    
    
    
    public Declarations get(ResolveableIdentifierLiteral name) throws ParseException {
        return this.get(name.getIdentifier(), name.getPosition());
    }
    
    

    public Map<String, Declarations> getNamespaces() {
        return this.namespaces;
    }
}