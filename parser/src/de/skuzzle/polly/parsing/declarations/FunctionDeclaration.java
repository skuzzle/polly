package de.skuzzle.polly.parsing.declarations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    
    

    public boolean isHardcoded() {
        return this.hardcoded;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append ("(FUNC) ");
        if (this.isGlobal()) {
            b.append("global");
        }
        b.append(this.getType());
        b.append(" ");
        b.append(this.getName());
        b.append("(");
        Iterator<VarDeclaration> it = this.formalParameters.iterator();
        while (it.hasNext()) {
            VarDeclaration decl = it.next();
            b.append(decl.getType());
            b.append(" ");
            b.append(decl.getName());
            if (it.hasNext()) {
                b.append(", ");
            }
        }
        b.append(")");
        return b.toString();
    }
}
