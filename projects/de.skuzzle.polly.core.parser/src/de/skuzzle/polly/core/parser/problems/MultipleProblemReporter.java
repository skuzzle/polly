package de.skuzzle.polly.core.parser.problems;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import de.skuzzle.polly.core.parser.ParseException;
import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.Token;
import de.skuzzle.polly.core.parser.TokenType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;

/**
 * Problem reporter implementation that supports multiple problems.
 * 
 * @author Simon Taddiken
 */
public class MultipleProblemReporter implements ProblemReporter {

    private final SortedSet<Problem> problems;
    
    
    
    public MultipleProblemReporter() {
        this.problems = new TreeSet<MultipleProblemReporter.Problem>();
    }
    
    
    
    /**
     * Gets a list of all reported problems.
     * 
     * @return List of problems.
     */
    public Collection<Problem> getProblems() {
        return this.problems;
    }
    
    
    
    @Override
    public boolean hasProblems() {
        return !this.problems.isEmpty();
    }

    
    
    @Override
    public Collection<Position> problemPositions() {
        final SortedSet<Position> result = new TreeSet<Position>();
        for (final Problem problem : this.problems) {
            result.add(problem.getPosition());
        }
        return result;
    }
    
    

    @Override
    public void lexicalProblem(String problem, Position position) 
            throws ParseException {
        this.problems.add(new Problem(LEXICAL, position, problem));
    }

    
    
    @Override
    public void syntaxProblem(String problem, Position position, Object...params) 
            throws ParseException {
        this.problems.add(new Problem(SYNTACTICAL, position, 
            Problems.format(problem, params)));
    }
    
    

    @Override
    public void syntaxProblem(TokenType expected, Token occurred, Position position)
            throws ParseException {
        this.problems.add(new Problem(SYNTACTICAL, position, 
            Problems.format(Problems.UNEXPECTED_TOKEN, occurred.toString(false, false),
                expected.toString())));
    }
    
    

    @Override
    public void semanticProblem(String problem, Position position, Object...params) 
            throws ParseException {
        this.problems.add(new Problem(SEMATICAL, position, 
            Problems.format(problem, params)));
    }

    
    
    @Override
    public void typeProblem(Type expected, Type occurred, Position position)
            throws ASTTraversalException {
        this.problems.add(new Problem(SEMATICAL, position, 
            Problems.format(Problems.TYPE_ERROR, expected.getName(), occurred.getName())));
    }
}
