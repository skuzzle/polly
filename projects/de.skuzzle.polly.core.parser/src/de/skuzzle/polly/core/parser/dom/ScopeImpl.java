package de.skuzzle.polly.core.parser.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.dom.ASTName;
import de.skuzzle.polly.dom.bindings.Binding;
import de.skuzzle.polly.dom.bindings.Scope;

public class ScopeImpl implements Scope {

    private Scope parentScope;
    private final Map<String, Binding> bindings;



    public ScopeImpl(Scope parentScope) {
        this.parentScope = parentScope;
        this.bindings = new HashMap<>();
    }



    /**
     * Sets the parent scope for this scope.
     * @param parent The new parent.
     */
    public void setParent(Scope parent) {
        this.parentScope = parent;
    }



    @Override
    public Scope getParent() {
        return this.parentScope;
    }



    @Override
    public Collection<? extends Binding> getBindings() {
        return this.bindings.values();
    }



    @Override
    public Binding findLocalBinding(ASTName name) {
        return this.bindings.get(name.getName());
    }



    @Override
    public Binding findBinding(ASTName name) {
        final Binding here = this.findLocalBinding(name);
        if (here != null) {
            return here;
        }
        if (this.parentScope != null) {
            return this.parentScope.findBinding(name);
        }
        return null;
    }



    @Override
    public Collection<? extends Binding> findBindings(ASTName name) {
        final List<Binding> result = new ArrayList<>();
        final Binding here = this.findLocalBinding(name);
        if (here != null) {
            result.add(here);
        }
        if (this.parentScope != null) {
            result.addAll(this.parentScope.findBindings(name));
        }
        return result;
    }
}
