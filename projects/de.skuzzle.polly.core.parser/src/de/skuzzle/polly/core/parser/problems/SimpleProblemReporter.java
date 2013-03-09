package de.skuzzle.polly.core.parser.problems;

import java.util.Collections;
import java.util.List;

import de.skuzzle.polly.core.parser.ParseException;
import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.SyntaxException;
import de.skuzzle.polly.core.parser.Token;
import de.skuzzle.polly.core.parser.TokenType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;

/**
 * Simple problem reporter that does not support multiple problems. Instead, it throws
 * an exception upon the first problem that occurrs.
 * 
 * @author Simon Taddiken
 */
public class SimpleProblemReporter implements ProblemReporter {

    private boolean problem;
    private Position position;
    
    
    
    @Override
    public boolean hasProblems() {
        return this.problem;
    }

    
    
    @Override
    public List<Position> problemPositions() {
        if (this.hasProblems()) {
            return Collections.singletonList(this.position);
        }
        return Collections.emptyList();
    }

    
    
    @Override
    public void lexicalProblem(String problem, Position position) 
            throws ParseException {
        this.problem = true;
        throw new ParseException(problem, position);
    }

    
    
    @Override
    public void syntaxProblem(String problem, Position position, Object...params) 
            throws ParseException {
        this.problem = true;
        throw new ParseException(Problems.format(problem, params), position);
    }

    
    
    @Override
    public void syntaxProblem(TokenType expected, Token occurred, Position position)
            throws ParseException {
        this.problem = true;
        throw new SyntaxException(expected, occurred, position);
    }

    
    
    @Override
    public void semanticProblem(String problem, Position position, Object...params) 
            throws ParseException {
        this.problem = true;
        throw new ParseException(Problems.format(problem, params), position);
    }

    
    
    @Override
    public void typeProblem(Type expected, Type occurred, Position position)
            throws ASTTraversalException {
        this.problem = true;
        throw new ASTTraversalException(position, 
            Problems.format(Problems.TYPE_ERROR, expected, occurred));
    }

}
