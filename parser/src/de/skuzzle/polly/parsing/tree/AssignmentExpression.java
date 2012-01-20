package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Parameter;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.Type;


public class AssignmentExpression extends BinaryExpression {

    private static final long serialVersionUID = 1L;

    private boolean isPublic;


    public AssignmentExpression(Expression leftOperand, Token operator,
            Expression rightOperand) {
        this(leftOperand, operator, rightOperand, false);
    }
    
    
    public AssignmentExpression(Expression leftOperand, Token operator,
            Expression rightOperand, boolean isPublic) {
        super(leftOperand, operator, rightOperand);
        this.isPublic = isPublic;
    }
    
    
    
    @Override
    public Expression contextCheck(Context context) throws ParseException {       
        if (this.rightOperand instanceof IdentifierLiteral) {
            IdentifierLiteral id = 
                (IdentifierLiteral) this.rightOperand.contextCheck(context);
            
            this.leftOperand = this.leftOperand.contextCheck(context);
            
            context.getCurrentNamespace().add(id, this.leftOperand, this.isPublic);
            this.setType(this.leftOperand.getType());
            
            return this.leftOperand;
        } else if (this.rightOperand instanceof FunctionDefinition) {
            // no context check here!
            FunctionDefinition func = (FunctionDefinition) this.rightOperand;
            
            /* Resolve parameter types and add them as "empty" Expressions to the
             * local declarations. That means that any occurrence of a parameter 
             * variable on the left hand side of the declaration is replaced by an
             * Expression that only returns the type specified on the right hand side
             * of the declaration.
             */
            context.getCurrentNamespace().enter();
            
            for (Parameter param : func.getFormalParameters()) {
                Type type = param.getTypeExpression().contextCheck(context).getType();
                ResolveableIdentifierLiteral ril = 
                    new ResolveableIdentifierLiteral(param.getName().getIdentifier());
                ril.setType(type);
                
                context.getCurrentNamespace().add(param.getName(), ril);
            }
            
            /* The name of the function being declared may not occur on the left side. 
             * So mark it as forbidden.
             */
            context.getCurrentNamespace().add(new ForbiddenFunction(func.getName()));

            // Check context, but do not replace the root of the left subtree
            Expression checked = this.leftOperand.contextCheck(context);
            context.getCurrentNamespace().leave();
            
            FunctionDefinition result = new FunctionDefinition(func.getName(), 
                    this.leftOperand, func.getFormalParameters());
            result.setType(checked.getType());

            context.getCurrentNamespace().add(result, this.isPublic);
            
            // Function definitions do not have a return value (yet?)
            this.setType(Type.UNKNOWN);
        }
        
        return this;
    }

    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {}
    

    
    @Override
    public Object clone() {
        return super.clone();
    }
}
