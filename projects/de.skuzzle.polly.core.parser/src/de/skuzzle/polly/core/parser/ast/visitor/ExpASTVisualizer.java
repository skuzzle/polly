package de.skuzzle.polly.core.parser.ast.visitor;

import java.io.PrintStream;

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
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
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
    public boolean visit(Root root) throws ASTTraversalException {
        switch (this.before(root)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printNode(root, "Root", root.getPosition().toString());
        this.dotBuilder.printNode(root.getCommand(), "Name: " + root.getCommand(), 
                root.getCommand().getPosition().toString());
        this.dotBuilder.printEdge(root, root.getCommand(), "command");
        for (final Expression exp : root.getExpressions()) {
            if (!exp.visit(this)) {
                return false;
            }
            this.dotBuilder.printEdge(root, exp, Unparser.toString(exp, this.formatter));
        }
        return this.after(root) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Call call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("Call", call);
        if (!call.getLhs().visit(this)) {
            return false;
        }
        if (!call.getRhs().visit(this)) {
            return false;
        }
        
        this.dotBuilder.printEdge(call, call.getLhs(), Unparser.toString(call.getLhs(), this.formatter));
        this.dotBuilder.printEdge(call, call.getRhs(), Unparser.toString(call.getRhs(), this.formatter));
        return this.after(call) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(OperatorCall call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("OpCall: " + call.getOperator().getId(), call);
        if (!call.getLhs().visit(this)) {
            return false;
        }
        if (!call.getRhs().visit(this)) {
            return false;
        }
        
        this.dotBuilder.printEdge(call, call.getLhs(), Unparser.toString(call.getLhs(), this.formatter));
        this.dotBuilder.printEdge(call, call.getRhs(), Unparser.toString(call.getRhs(), this.formatter));
        return this.after(call) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ListLiteral list) throws ASTTraversalException {
        switch (this.before(list)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("List", list);
        
        int i = 0;
        for (final Expression exp : list.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
            this.dotBuilder.printEdge(list, exp, "" + (i++));
        }
        return this.after(list) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Literal literal) throws ASTTraversalException {
        switch (this.before(literal)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression(literal.toString(), literal);
        return this.after(literal) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ProductLiteral product) throws ASTTraversalException {
        switch (this.before(product)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("Product", product);
        
        int i = 0;
        for (final Expression exp : product.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
            this.dotBuilder.printEdge(product, exp, "" + (i++));
        }
        return this.after(product) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(FunctionLiteral func) throws ASTTraversalException {
        switch (this.before(func)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.dotBuilder.printExpression("Function", func);
        for (final Declaration param : func.getFormal()) {
            if (!param.visit(this)) {
                return false;
            }
            this.dotBuilder.printEdge(func, param, "param");
        }
        if (!func.getBody().visit(this)) {
            return false;
        }
        this.dotBuilder.printEdge(func, func.getBody(), "body");
        
        for (final Declaration param : func.getFormal()) {
            for (final VarAccess va : param.getUsage()) {
                this.dotBuilder.printUsage(va, param);
            }
        }
        
        return this.after(func) == CONTINUE;
    }

    
    
    @Override
    public boolean visit(Assignment assign) throws ASTTraversalException {
        switch (this.before(assign)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.dotBuilder.printExpression("Assignment", assign);
        if (!assign.getExpression().visit(this)) {
            return false;
        }
        this.dotBuilder.printNode(assign.getName(), "Name: " + assign.getName(), 
            assign.getName().getPosition().toString());
        this.dotBuilder.printEdge(assign, assign.getExpression(), "expresion");
        this.dotBuilder.printEdge(assign, assign.getName(), "to");
        
        return this.after(assign) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.dotBuilder.printExpression("Namespace Access", access);
        if (!access.getLhs().visit(this)) {
            return false;
        }
        if (!access.getRhs().visit(this)) {
            return false;
        }
        this.dotBuilder.printEdge(access, access.getLhs(), "LHS");
        this.dotBuilder.printEdge(access, access.getRhs(), "RHS");
        
        return this.after(access) == CONTINUE;
    }
    
    
    
    @Override
    public int before(VarAccess access) throws ASTTraversalException {
        this.dotBuilder.printExpression("VarAccess: " + access.getIdentifier(), access);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Problem problem) throws ASTTraversalException {
        this.dotBuilder.printExpression("PROBLEM", problem);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Declaration decl) throws ASTTraversalException {
        final StringBuilder b = new StringBuilder();
        b.append("Usage (by name):\\n");
        for (final VarAccess va : decl.getUsage()) {
            b.append(va.getPosition());
            b.append("\\n");
        }
        String pos = decl.getPosition() == Position.NONE 
            ? "" : " (" + decl.getPosition().toString() + ")";
        
        this.dotBuilder.printNode(decl, "Declaration" + pos ,  
            "Name: " + decl.getName(), 
            "Type: " + decl.getType(),
            b.toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Braced braced) throws ASTTraversalException {
        switch (this.before(braced)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("Embraced", braced);
        if (!braced.getExpression().visit(this)) {
            return false;
        }
        this.dotBuilder.printEdge(braced, braced.getExpression(), "");
        
        return this.after(braced) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Delete delete) throws ASTTraversalException {
        switch (this.before(delete)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.dotBuilder.printExpression("Delete", delete);
        for (final Identifier id : delete.getIdentifiers()) {
            this.dotBuilder.printNode(id, id.getId());
            this.dotBuilder.printEdge(delete, id, "");
        }
        return this.after(delete) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect inspect) throws ASTTraversalException {
        switch (this.before(inspect)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.dotBuilder.printExpression("Inspect", inspect);
        if (!inspect.getAccess().visit(this)) {
            return false;
        }
        this.dotBuilder.printEdge(inspect, inspect.getAccess(), "");
        return this.after(inspect) == CONTINUE;
    }
}
