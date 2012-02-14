package de.skuzzle.polly.parsing.tree.literals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.Expression;




public class ListLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private java.util.List<Expression> elements;
    
    public ListLiteral(Token token) {
        super(token, new ListType(Type.UNKNOWN));
        this.elements = new ArrayList<Expression>();
    }
    
    
    
    public ListLiteral(java.util.List<Expression> expressions) {
        super(new Token(TokenType.LIST, Position.EMPTY), new ListType(Type.UNKNOWN));
        this.elements = expressions;
    }
    
    
    
    public ListLiteral(java.util.List<Expression> expressions, Type subType) {
        this(expressions);
        this.setType(new ListType(subType));
    }
    
    
    
    public java.util.List<Expression> getElements() {
        return this.elements;
    }

    
    
    public Type getElementType() {
        return this.elements.isEmpty() ? Type.UNKNOWN : this.elements.get(0).getType();
    }
    
    
    
    
    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        if (!this.elements.isEmpty()) {
            Expression first = this.elements.get(0);
            first = first.contextCheck(context);
            ListType listT = new ListType(first.getType());
            
            this.setType(listT);
            
            java.util.List<Expression> checkedElements = new ArrayList<Expression>();
            for (Expression lit : this.elements) {
                lit = lit.contextCheck(context);
                if (!lit.getType().check(first.getType())) {
                    throw new ParseException(
                            "Listen dürfen nur Elemente des gleichen Typs enthalten",
                            lit.getPosition());
                }
                checkedElements.add(lit);
            }
            this.elements = checkedElements;
        } else {
            this.setType(Type.EMPTY_LIST);
        }
        
        return this;
    }
    
    
    // ISSUE: 0000048 Added cast from list to string.
    @Override
    public Literal castTo(Type target) throws ExecutionException {
        if (target.check(Type.STRING)) {
            return new StringLiteral(this.toString(false));
        }
        return super.castTo(target);
    }
    
    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
    	java.util.List<Expression> tempList = new ArrayList<Expression>();
        Stack<Literal> s = new Stack<Literal>();
        
        /*
         * Replace all expressions with their result - which is always a literal! 
         */
        for (Expression e : this.elements) {
            e.collapse(s);
            tempList.add(s.pop());
        }
        
        this.elements = tempList;
        stack.push(this);
    }
    
    
    
    @Override
    public String toString() {
        return this.toString(true);
    }
    
    
    
    public String toString(boolean braces) {
        StringBuilder result = new StringBuilder();
        
        if (braces) {
            result.append("{");
        }
        Iterator<Expression> it = this.elements.iterator();
        while (it.hasNext()) {
            result.append(it.next().toString());
            if (it.hasNext()) {
                result.append(",");
            }
        }
        if (braces) {
            result.append("}");
        }
        return result.toString();
    }


    
    @Override
    public int compareTo(Literal o) {
        if (o instanceof ListLiteral) {
            ListLiteral other = (ListLiteral) o;
            
            return ((Integer) this.getElements().size()).compareTo(
                    other.getElements().size());
        }
        throw new RuntimeException("Not compareable");
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ListLiteral other = (ListLiteral) obj;
        if (this.elements == null) {
            if (other.elements != null) {
                return false;
            }
        } else if (!this.elements.equals(other.elements)) {
            return false;
        }
        return true;
    }
}
