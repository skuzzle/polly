package de.skuzzle.polly.parsing.tree;

import java.util.List;
import java.util.Stack;


import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Declaration;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.ResolvableIdentifierLiteral;
import de.skuzzle.polly.parsing.tree.literals.UserLiteral;

public class VarOrCall extends Expression {

	private ResolvableIdentifierLiteral id;
	private Declaration resolvedDeclaration;
	private Expression resolvedExpression;
	private List<Expression> actualParameters;
	private boolean hardcoded;

	private static final long serialVersionUID = 1L;

	public VarOrCall(Position position) {
		super(position);
	}
	
	
	
	public List<Expression> getActualParameters() {
        return this.actualParameters;
    }

	
	
	@Override
	public Expression contextCheck(Namespace context) throws ParseException {
	    // Replace this expression with this expression preceded by a NameSpaceAccess
	    // with the root namespace
		this.resolvedDeclaration = context.resolve(this.id);
		Expression result = new NamespaceAccessExpression(new UserLiteral(
				context.getRootNS()), this, this.getPosition());
		
		result = result.contextCheck(context);
		return result;
	}
	
	
	
	public void contextCheckForMember(Namespace context) throws ParseException {
	    if (this.resolvedDeclaration instanceof FunctionDeclaration) {
	        FunctionDeclaration decl = (FunctionDeclaration) this.resolvedDeclaration;
	        
	        if (decl.getFormalParameters().size() != this.actualParameters.size()) {
	            throw new ParseException("Falsche Parameteranzahl: " + 
	                    this.actualParameters.size() + ". Erwartet: " + 
	                    decl.getFormalParameters().size(), this.getPosition());
	        }

	        context.enter();
	        try {
	            for (int i = 0; i < this.actualParameters.size(); ++i) {
	                Expression actual = this.actualParameters.get(i);
	                VarDeclaration formal = decl.getFormalParameters().get(i);
	                
	                actual = actual.contextCheck(context);
	                this.actualParameters.set(i, actual);
	                
	                if (!formal.getType().check(actual.getType())) {
	                    Type.typeError(actual.getType(), formal.getType(), 
	                        actual.getPosition());
	                }
	                
	                // declare a new var for each formal parameter which contains
	                // the expression of the actual parameter
	                VarDeclaration act = new VarDeclaration(formal.getName(), actual);
	                context.add(act);
	            }
	            
	            this.hardcoded = decl.isHardcoded();
	            
	            // For non hardcoded functions, this will cause all parameters in the
	            // expression to be replaced by their actual expression.
	            // hardcoded functions will do nothing
	            this.resolvedExpression = decl.getExpression().contextCheck(context);
	        } finally {
	            // make sure to leave the declarations in a clean state
	            context.leave();
	        }
	        
	        this.setType(this.resolvedExpression.getType());
	    } else if (this.resolvedDeclaration instanceof VarDeclaration) {
	        this.resolvedExpression = ((VarDeclaration) this.resolvedDeclaration)
	                .getExpression().contextCheck(context);
	        this.setType(this.resolvedExpression.getType());
	    } else {
	        assert false;
	    }
	}


	
	@Override
	public void collapse(Stack<Literal> stack) throws ExecutionException {
		// TODO Auto-generated method stub

	}

	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

}
