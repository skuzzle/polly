package de.skuzzle.polly.parsing.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;




public class Root implements TreeElement {
    
    private static final long serialVersionUID = 1L;
    
    private CommandLiteral name;
    private boolean collapsed;
    private List<Expression> parameters;
    private List<Literal> results;
    
    
    public Root(CommandLiteral name) {
        this.name = name;
        this.parameters = new ArrayList<Expression>();
        this.results = new LinkedList<Literal>();
    }
    
    
    
    public CommandLiteral getName() {
        return this.name;
    }
    
    
    
    public List<Expression> getParameters() {
        return this.parameters;
    }
    
    
    
    public List<Literal> getResults() {
        if (!collapsed) {
            throw new RuntimeException("Expressions not collapsed.");
        }
        
        return this.results;
    }
    

    @Override
    public Expression contextCheck(Context context) throws ParseException {
        for (int i = 0; i < this.parameters.size(); ++i) {
            Expression e = this.parameters.get(i);
            this.parameters.set(i, e.contextCheck(context));
        }
        
        return null;
    }

    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        for (Expression e : this.parameters) {
            e.collapse(stack);
            
            if (!stack.isEmpty()) {
                this.results.add(stack.pop());
            }
        }
        
        this.collapsed = true;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        if (this.collapsed) {
            for (Literal r : this.results) {
                result.append(r.toString());
                result.append(" ");
            }
        }
        
        return result.toString();
    }


}
