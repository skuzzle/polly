package de.skuzzle.polly.core.parser.problems;

import java.util.Collection;

import de.skuzzle.polly.core.parser.ParseException;
import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.Token;
import de.skuzzle.polly.core.parser.TokenType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;


/**
 * Interface to report problems that might occur during scanning, parsing or
 * type checking. Implementors may choose to only support reporting of one
 * problem by throwing an exception upon reporting. They may also support
 * multiple problems.
 * 
 * @author Simon Taddiken
 */
public interface ProblemReporter {
    
    /**
     * Represents a single problem.
     * 
     * @author Simon Taddiken
     */
    public final static class Problem implements Comparable<Problem> {
        protected final int type;
        protected final Position position;
        protected final String message;
        
        
        
        public Problem(int type, Position position, String message) {
            if (type != LEXICAL && type != SYNTACTICAL && type != SEMATICAL) {
                throw new IllegalArgumentException("illegal problem type");
            }
            this.type = type;
            this.position = position;
            this.message = message;
        }

        
        
        /**
         * Gets the problem type. Either of
         * {@link ProblemReporter#LEXICAL},
         * {@link ProblemReporter#SYNTACTICAL},
         * {@link ProblemReporter#SEMATICAL}
         * 
         * @return The problem type.
         */
        public int getType() {
            return this.type;
        }
        
        
        
        /**
         * Gets the position of this problem.
         * 
         * @return The problem position.
         */
        public Position getPosition() {
            return this.position;
        }

        
        
        /**
         * Gets the error message of this problem.
         * 
         * @return The problem message.
         */
        public String getMessage() {
            return this.message;
        }
        
        
        
        @Override
        public int compareTo(Problem o) {
            return this.position.compareTo(o.position);
        }
    }
    
    
    
    /** Constant representing lexical problems */
    public final static int LEXICAL = 0;
    
    /** Constant representing syntactical problems */
    public final static int SYNTACTICAL = 1;
    
    /** Constant representing semantical problems */
    public final static int SEMATICAL = 2;
    
    
    
    /**
     * Whether at least one problem has been reported.
     * 
     * @return Whether at least one problem has been reported.
     */
    public boolean hasProblems();
    
    /**
     * Gets a collection of all positions within the source where errors occurred. The 
     * resulting collection will be sorted.
     * 
     * @return Collection of problem locations.
     */
    public Collection<Position> problemPositions();
    
    public void lexicalProblem(String problem, Position position) 
        throws ParseException;

    public void syntaxProblem(String problem, Position position) 
        throws ParseException;
    
    public void syntaxProblem(TokenType expected, Token occurred, 
        Position position) throws ParseException;
    
    public void semanticProblem(String problem, Position position) throws ParseException;
    
    public void typeProblem(Type expected, Type occurred, Position position) 
        throws ASTTraversalException;
}