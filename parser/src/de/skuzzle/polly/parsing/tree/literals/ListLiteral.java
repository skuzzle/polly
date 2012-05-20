package de.skuzzle.polly.parsing.tree.literals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Field;
import de.skuzzle.polly.parsing.util.Fields;
import de.skuzzle.polly.parsing.util.Matrix;




public class ListLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private List<Expression> elements;
    
    public ListLiteral(Token token) {
        super(token, new ListType(Type.UNKNOWN));
        this.elements = new ArrayList<Expression>();
    }
    
    
    
    public ListLiteral(List<Expression> expressions) {
        super(new Token(TokenType.LIST, Position.EMPTY), new ListType(Type.UNKNOWN));
        this.elements = expressions;
    }
    
    
    
    public ListLiteral(List<Expression> expressions, Type subType) {
        this(expressions);
        this.setType(new ListType(subType));
    }
    
    
    
    public ListLiteral(Matrix<?> matrix) {
        super(new Token(TokenType.LIST, Position.EMPTY), new ListType(
            new ListType(Type.NUMBER)));
        
        this.elements = new ArrayList<Expression>();
        for (int i = 0; i < matrix.getM(); ++i) {
            ArrayList<Expression> row = new ArrayList<Expression>();
            for (int j = 0; j < matrix.getN(); ++j) {
                row.add(new NumberLiteral((Number) matrix.get(i, j)));
            }
            this.elements.add(new ListLiteral(row));
        }
    }
    
    
    
    public List<Expression> getElements() {
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
            
            List<Expression> checkedElements = new ArrayList<Expression>();
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
    
    
    
    public boolean checkIsValidMatrix() {
        if (this.elements.isEmpty()) {
            return true;
        }
        if (!this.elements.get(0).getType().check(new ListType(Type.NUMBER))) {
            return false;
        }
        int dimension = ((ListLiteral) this.elements.get(0)).getElements().size();
        for (Expression e : this.elements) {
            if (((ListLiteral) e).getElements().size() != dimension) {
                return false;
            }
        }
        return true;
    }
    
    
    
    public Matrix<Double> toDoubleMatrix() throws ExecutionException {
        int cols = ((ListLiteral) this.elements.get(0)).getElements().size();
        Matrix<Double> result = new Matrix<Double>(
                this.elements.size(), cols, Fields.DOUBLE);
        
        int i = 0;
        int j = 0;
        for (Expression e : this.elements) {
            ListLiteral sub = (ListLiteral) e;
            for (Expression e1 : sub.getElements()) {
                NumberLiteral num = (NumberLiteral) e1;
                result.set(i, j++, num.getValue());
            }
            ++i;
            j = 0;
        }
        return result;
    }
    
    
    
    public Matrix<Integer> toIntMatrix(Field<Integer> field) throws ExecutionException {
        int cols = ((ListLiteral) this.elements.get(0)).getElements().size();
        Matrix<Integer> result = new Matrix<Integer>(
                this.elements.size(), cols, field);
        
        int i = 0;
        int j = 0;
        for (Expression e : this.elements) {
            ListLiteral sub = (ListLiteral) e;
            for (Expression e1 : sub.getElements()) {
                NumberLiteral num = (NumberLiteral) e1;
                result.set(i, j++, num.isInteger());
            }
            ++i;
            j = 0;
        }
        return result;
    }
    
    
    // ISSUE: 0000048 Added cast from list to string.
    @Override
    public Literal castTo(Type target) throws ExecutionException {
        if (target.check(Type.STRING)) {
            return new StringLiteral(this.toString(false));
        } else if (target instanceof ListType) {
            ListType l = (ListType) target;
            ListLiteral result = new ListLiteral(new ArrayList<Expression>(
                this.elements.size()), l.getSubType());
            for (Expression e : this.elements) {
                result.getElements().add(((Literal) e).castTo(l.getSubType()));
            }
            return result;
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
                result.append(", ");
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((this.elements == null) ? 0 : this.elements.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ListLiteral)) {
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
