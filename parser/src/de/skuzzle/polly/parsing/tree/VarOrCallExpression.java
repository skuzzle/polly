package de.skuzzle.polly.parsing.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Declaration;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.ResolvableIdentifierLiteral;
import de.skuzzle.polly.parsing.tree.literals.UserLiteral;

public class VarOrCallExpression extends Expression {

	private ResolvableIdentifierLiteral id;
	private Declaration resolvedDeclaration;
	private Expression resolvedExpression;
	private List<Expression> actualParameters;
	private boolean hardcoded;

	private static final long serialVersionUID = 1L;

	public VarOrCallExpression(ResolvableIdentifierLiteral id) {
		super(id.getPosition());
		this.id = id;
	}
	
	
	
    public ResolvableIdentifierLiteral getId() {
        return this.id;
    }
	
	
	
	public List<Expression> getActualParameters() {
	    // lazy init, becaus this might aswell be a Var-access where no
	    // parameters are needed.
	    if (this.actualParameters == null) {
	        this.actualParameters = new ArrayList<Expression>();
	    }
        return this.actualParameters;
    }

	
	
	@Override
	public Expression contextCheck(Namespace context) throws ParseException {
	    // Precede this call with a namespace access if this call does not
	    // reference a local declaration.
	    this.contextCheckForMember(context);
	    if (!((VarDeclaration)this.resolvedDeclaration).isLocal()) {
    		Expression result = new NamespaceAccessExpression(new UserLiteral(
    				context.getRootNS()), this, this.getPosition());
    		
    		result = result.contextCheck(context);
    		return result;
	    }
	    return this;
	}
	
	
	
	public void contextCheckForMember(Namespace context) 
	            throws ParseException {
	    this.resolvedDeclaration = context.resolve(this.id);
	    
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
	                
	                // declare a new local var for each formal parameter which contains
	                // the expression of the actual parameter
	                VarDeclaration act = new VarDeclaration(
	                            formal.getName(), actual, true);
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
	        if (this.actualParameters != null && !this.actualParameters.isEmpty()) {
	            throw new ParseException(this.id + 
	                " ist eine Variable und kann nicht mit Parametern aufgerufen werden", 
	                this.getPosition());
	        }
	        this.resolvedExpression = ((VarDeclaration) this.resolvedDeclaration)
	                .getExpression().contextCheck(context);
	        this.setType(this.resolvedExpression.getType());
	    } else {
	        assert false;
	    }
	}


	
	@Override
	public void collapse(Stack<Literal> stack) throws ExecutionException {
		if (this.resolvedDeclaration instanceof FunctionDeclaration) {
		    // Hardcoded functions expect their params to be on the stack
		    if (this.hardcoded) {
		        for (Expression exp : this.actualParameters) {
		            exp.collapse(stack);
		        }
		    }
		}
		
		// this is the same for vars and functions
	    this.resolvedExpression.collapse(stack);
	}
}
