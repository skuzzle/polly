package de.skuzzle.polly.core.parser.ast.visitor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Braced;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Empty;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.Problem;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ChannelLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.DateLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.HelpLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.LiteralFormatter;
import de.skuzzle.polly.core.parser.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.UserLiteral;
import de.skuzzle.polly.core.parser.util.ASTDotBuilder;


public class ExpASTVisualizer extends DepthFirstVisitor {
    
    
    private ASTDotBuilder dotBuilder;
    private final LiteralFormatter formatter;
    
    
    
    public ExpASTVisualizer() {
        this.formatter = new LiteralFormatter() {
            
            @Override
            public String formatUser(UserLiteral user) {
                return DEFAULT.formatUser(user);
            }
            
            
            
            @Override
            public String formatTimespan(TimespanLiteral timespan) {
                return DEFAULT.formatTimespan(timespan);
            }
            
            
            
            @Override
            public String formatString(StringLiteral string) {
                return string.getValue();
            }
            
            
            
            @Override
            public String formatNumberLiteral(NumberLiteral number) {
                return DEFAULT.formatNumberLiteral(number);
            }
            
            
            
            @Override
            public String formatList(ListLiteral listLiteral) {
                return DEFAULT.formatList(listLiteral);
            }
            
            
            
            @Override
            public String formatFunction(FunctionLiteral functionLiteral) {
                return "\\" + DEFAULT.formatFunction(functionLiteral);
            }
            
            
            
            @Override
            public String formatDate(DateLiteral date) {
                return DEFAULT.formatDate(date);
            }
            
            
            
            @Override
            public String formatChannel(ChannelLiteral channel) {
                return DEFAULT.formatChannel(channel);
            }



            @Override
            public String formatHelp(HelpLiteral helpLiteral) {
                return DEFAULT.formatHelp(helpLiteral);
            }
        };
    }
    
    
    
    public void visualize(Node root, PrintStream out, Namespace ns) 
            throws ASTTraversalException {
        this.dotBuilder = new ASTDotBuilder(out);
        //this.dotBuilder.printNameSpace(ns);
        root.visit(this);
        this.dotBuilder.finish();
    }
    
    
    
    @Override
    public boolean visit(Root node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printNode(node, "Root", node.getPosition().toString());
        this.dotBuilder.printNode(node.getCommand(), "Name: " + node.getCommand(), 
            node.getCommand().getPosition().toString());
        this.dotBuilder.printEdge(node, node.getCommand(), "command");
        for (final Expression exp : node.getExpressions()) {
            if (!exp.visit(this)) {
                return false;
            }
            this.dotBuilder.printEdge(node, exp, Unparser.toString(exp, this.formatter));
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Call node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("Call", node);
        if (!node.getLhs().visit(this)) {
            return false;
        }
        if (!node.getRhs().visit(this)) {
            return false;
        }
        
        this.dotBuilder.printEdge(node, node.getLhs(), Unparser.toString(node.getLhs(), this.formatter));
        this.dotBuilder.printEdge(node, node.getRhs(), Unparser.toString(node.getRhs(), this.formatter));
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(OperatorCall node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("OpCall: " + node.getOperator().getId(), node);
        if (!node.getLhs().visit(this)) {
            return false;
        }
        if (!node.getRhs().visit(this)) {
            return false;
        }
        
        this.dotBuilder.printEdge(node, node.getLhs(), Unparser.toString(node.getLhs(), this.formatter));
        this.dotBuilder.printEdge(node, node.getRhs(), Unparser.toString(node.getRhs(), this.formatter));
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ListLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("List", node);
        
        int i = 0;
        for (final Expression exp : node.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
            this.dotBuilder.printEdge(node, exp, "" + (i++));
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Literal node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression(node.toString(), node);
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ProductLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("Product", node);
        
        int i = 0;
        for (final Expression exp : node.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
            this.dotBuilder.printEdge(node, exp, "" + (i++));
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(FunctionLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.dotBuilder.printExpression("Function", node);
        for (final Declaration param : node.getFormal()) {
            if (!param.visit(this)) {
                return false;
            }
            this.dotBuilder.printEdge(node, param, "param");
        }
        if (!node.getBody().visit(this)) {
            return false;
        }
        this.dotBuilder.printEdge(node, node.getBody(), "body");
        
        for (final Declaration param : node.getFormal()) {
            for (final VarAccess va : param.getUsage()) {
                this.dotBuilder.printUsage(va, param);
            }
        }
        
        return this.after(node) == CONTINUE;
    }

    
    
    @Override
    public boolean visit(Assignment node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.dotBuilder.printExpression("Assignment", node);
        if (!node.getExpression().visit(this)) {
            return false;
        }
        this.dotBuilder.printNode(node.getName(), "Name: " + node.getName(), 
            node.getName().getPosition().toString());
        this.dotBuilder.printEdge(node, node.getExpression(), "expresion");
        this.dotBuilder.printEdge(node, node.getName(), "to");
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.dotBuilder.printExpression("Namespace Access", node);
        if (!node.getLhs().visit(this)) {
            return false;
        }
        if (!node.getRhs().visit(this)) {
            return false;
        }
        this.dotBuilder.printEdge(node, node.getLhs(), "LHS");
        this.dotBuilder.printEdge(node, node.getRhs(), "RHS");
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public int before(VarAccess node) throws ASTTraversalException {
        final StringBuilder b = new StringBuilder();
        final Declaration vd = node.getIdentifier().getDeclaration();
        
        b.append("VarAccess: ");
        b.append(node.getIdentifier());
        if(vd != null) {
            final List<String> attributes = new ArrayList<String>(5);
            if (vd.isLocal()) attributes.add("local");
            if (vd.isNative()) attributes.add("native");
            if (vd.isPublic()) attributes.add("public");
            b.append(" (");
            final Iterator<String> it = attributes.iterator();
            while (it.hasNext()) {
                b.append(it.next());
                if (it.hasNext()) {
                    b.append(", ");
                }
            }
            b.append(")");
        }
        
        
        this.dotBuilder.printExpression(b.toString(), node);
        if (vd != null && !vd.isLocal() && !vd.isNative() && 
                !(vd.getExpression() instanceof Empty)) {
            
            vd.getExpression().visit(this);
            this.dotBuilder.printEdge(node, vd.getExpression(), "");
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Problem node) throws ASTTraversalException {
        this.dotBuilder.printExpression("PROBLEM", node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Declaration node) throws ASTTraversalException {
        final StringBuilder b = new StringBuilder();
        b.append("Usage (by name):\\n");
        for (final VarAccess va : node.getUsage()) {
            b.append(va.getPosition());
            b.append("\\n");
        }
        String pos = node.getPosition() == Position.NONE 
            ? "" : " (" + node.getPosition().toString() + ")";
        
        this.dotBuilder.printNode(node, "Declaration" + pos ,  
            "Name: " + node.getName(), 
            "Type: " + node.getType(),
            b.toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Braced node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("Embraced", node);
        if (!node.getExpression().visit(this)) {
            return false;
        }
        this.dotBuilder.printEdge(node, node.getExpression(), "");
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Delete node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.dotBuilder.printExpression("Delete", node);
        for (final Identifier id : node.getIdentifiers()) {
            this.dotBuilder.printNode(id, id.getId());
            this.dotBuilder.printEdge(node, id, "");
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("Inspect", node);
        if (!node.getAccess().visit(this)) {
            return false;
        }
        this.dotBuilder.printEdge(node, node.getAccess(), "");
        return this.after(node) == CONTINUE;
    }
}
