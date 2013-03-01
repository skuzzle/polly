package de.skuzzle.polly.parsing.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;

/**
 * Problem reporter implementation that supports multiple problems.
 * 
 * @author Simon Taddiken
 */
public class MultipleProblemReporter implements ProblemReporter {

    private final List<Problem> problems;
    
    
    
    public MultipleProblemReporter() {
        this.problems = new ArrayList<MultipleProblemReporter.Problem>();
    }
    
    
    
    /**
     * Gets a list of all reported problems.
     * 
     * @return List of problems.
     */
    public List<Problem> getProblems() {
        return this.problems;
    }
    
    
    
    @Override
    public boolean hasProblems() {
        return !this.problems.isEmpty();
    }

    
    
    @Override
    public List<Position> problemPositions() {
        final List<Position> result = new ArrayList<Position>(this.problems.size());
        for (final Problem problem : this.problems) {
            result.add(problem.getPosition());
        }
        Collections.sort(result);
        return result;
    }
    
    

    @Override
    public void lexicalProblem(String problem, Position position) throws ParseException {
        this.problems.add(new Problem(LEXICAL, position, problem));
    }

    
    
    @Override
    public void syntaxProblem(String problem, Position position) throws ParseException {
        this.problems.add(new Problem(SYNTACTICAL, position, problem));
    }
    
    

    @Override
    public void syntaxProblem(TokenType expected, Token occurred, Position position)
            throws ParseException {
        this.problems.add(new Problem(SYNTACTICAL, position, "Unerwartetes Symbol: '" + 
            occurred.toString(false, false) + 
            "'. Erwartet: '" + expected.toString() + "'"));
    }
    
    

    @Override
    public void semanticProblem(String problem, Position position) throws ParseException {
        this.problems.add(new Problem(SEMATICAL, position, problem));
    }

    
    
    @Override
    public void typeProblem(Type expected, Type occurred, Position position)
            throws ASTTraversalException {
        this.problems.add(new Problem(SEMATICAL, position, "Typefehler. Erwartet: " + 
            expected + ", gefunden: " + occurred));
    }

}
