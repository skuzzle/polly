package de.skuzzle.polly.parsing.declarations;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class FunctionDeclaration extends VarDeclaration {

    private static final long serialVersionUID = 1L;
    private List<VarDeclaration> formalParameters;
    private boolean hardcoded;

    
    
    public FunctionDeclaration(IdentifierLiteral name, boolean global, boolean temp) {
        super(name, global, temp);
        this.formalParameters = new ArrayList<VarDeclaration>();
    }
    
    
    
    
    public FunctionDeclaration(IdentifierLiteral name, boolean hardcoded) {
        this(name, false, false);
        this.hardcoded = true;
    }

    
    
    
    public List<VarDeclaration> getFormalParameters() {
        return this.formalParameters;
    }
    
    
    
    @Override
    public void contextCheck(Namespace c) throws ParseException {
        super.contextCheck(c);
        
        for (VarDeclaration vardecl : this.formalParameters) {
            vardecl.contextCheck(c);
        }
    }
    
    
    
    
    public boolean isHardcoded() {
        return this.hardcoded;
    }
    
    
    @Override
    public String toString() {
        // TODO: parameter list
        return "(FUNC) " + (this.isGlobal() ? "global" : "") + this.getType() + " " + 
            this.getName();
    }
}
