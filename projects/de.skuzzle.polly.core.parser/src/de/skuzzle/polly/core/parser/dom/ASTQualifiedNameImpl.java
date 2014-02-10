package de.skuzzle.polly.core.parser.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.parser.Span;
import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.polly.dom.ASTName;
import de.skuzzle.polly.dom.ASTQualifiedName;
import de.skuzzle.polly.dom.ASTVisitor;

public class ASTQualifiedNameImpl extends AbstractPollyNode implements ASTQualifiedName {

    /** Separator of names within a qualified name */
    public final static String QULIFICATION_SEPARATOR = "."; //$NON-NLS-1$



    private static String qualifiedString(String qualificationSeparator,
            List<ASTName> names) {
        if (names.isEmpty()) {
            throw new IllegalArgumentException("qualification may not be empty"); //$NON-NLS-1$
        }
        final StringBuilder b = new StringBuilder();
        final Iterator<ASTName> nameIt = names.iterator();
        while (nameIt.hasNext()) {
            b.append(nameIt.next().getName());
            if (nameIt.hasNext()) {
                b.append(qualificationSeparator);
            }
        }
        return b.toString();
    }

    
    
    
    /** Names of this qualified name */
    private final List<ASTName> names;



    ASTQualifiedNameImpl(ASTName firstName) {
        if (firstName == null) {
            throw new NullPointerException("firstName"); //$NON-NLS-1$
        }
        assertNotFrozen(firstName);
        this.names = new ArrayList<>();
        this.names.add(firstName);
    }



    /**
     * Private constructor for {@link #copy()}
     */
    private ASTQualifiedNameImpl() {
        this.names = new ArrayList<>();
    }

    
    @Override
    public ASTQualifiedName getOrigin() {
        return super.getOrigin().as(ASTQualifiedName.class);
    }
    

    
    @Override
    public ASTQualifiedName deepOrigin() {
        return super.deepOrigin().as(ASTQualifiedName.class);
    }
    
    
    
    @Override
    public ASTQualifiedNameImpl copy() {
        // HACK: can not use node factory here
        final ASTQualifiedNameImpl copy = new ASTQualifiedNameImpl();
        for (final ASTName name : this.names) {
            copy.addName(name.copy());
        }
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        copy.setSyntax(this.getSyntax());
        return copy;
    }



    @Override
    public List<ASTName> getChildren() {
        return this.getNames();
    }



    @Override
    public List<ASTName> getNames() {
        return Collections.unmodifiableList(this.names);
    }



    @Override
    public ASTName getLastName() {
        assert !this.names.isEmpty();
        return this.names.get(this.names.size() - 1);
    }



    @Override
    public void addName(ASTName name) {
        this.assertNotFrozen();
        
        if (name == null) {
            throw new NullPointerException("name"); //$NON-NLS-1$
        }
        this.assertNotFrozen(name);
        if (name == this) {
            throw new IllegalArgumentException("can not add name to itself"); //$NON-NLS-1$
        }
        
        this.names.add(name);
        name.setPropertyInParent(PART_OF_QUALIFIED_NAME);
        name.setParent(this);
        if (this.getLocation() != null && name.getLocation() != null) {
            this.setLocation(new Span(this.getLocation(), name.getLocation()));
        }
    }



    @Override
    public String getName() {
        return qualifiedString(QULIFICATION_SEPARATOR, this.names);
    }



    @Override
    public void updateRelationships(boolean deep) {
        for (final ASTName name : this.names) {
            name.setPropertyInParent(PART_OF_QUALIFIED_NAME);
            name.setParent(this);
            if (deep) {
                name.updateRelationships(deep);
            }
        }
    }



    @Override
    public void replaceChild(ASTNode<ASTVisitor> child, ASTNode<ASTVisitor> newChild) {
        this.assertNotFrozen();
        this.assertNotFrozen(newChild);

        if (newChild == child) {
            return;
        } else if (newChild == this) {
            throw new IllegalArgumentException(ERROR_SELF_AS_CHILD);
        } else if (this.getParent() != null && newChild == this.getParent()) {
            throw new IllegalArgumentException(ERROR_PARENT_AS_CHILD);
        } else if (!newChild.is(ASTName.class)) {
            throw new IllegalArgumentException(ERROR_NOT_EXPECTED_TYPE);
        }
        for (int i = 0; i < this.names.size(); ++i) {
            if (this.names.get(i) == child) {
                final ASTName nc = newChild.as(ASTName.class);
                nc.setPropertyInParent(PART_OF_QUALIFIED_NAME);
                nc.setParent(this);
                this.names.set(i, nc);
                return;
            }
        }
        throw new IllegalArgumentException(ERROR_NOT_A_CHILD);
    }



    @Override
    public boolean accept(ASTVisitor visitor) {
        if (visitor.shouldVisitQualifiedNames) {
            switch (visitor.visit(this)) {
            case PROCESS_SKIP:  return true;
            case PROCESS_ABORT: return false;
            }
        }

        for (int i = 0; i < this.names.size(); ++i) {
            final ASTName child = this.names.get(i);
            if (!child.accept(visitor)) {
                return false;
            }
        }

        if (visitor.shouldVisitQualifiedNames) {
            return visitor.leave(this) != PROCESS_ABORT;
        }
        return true;
    }
}
