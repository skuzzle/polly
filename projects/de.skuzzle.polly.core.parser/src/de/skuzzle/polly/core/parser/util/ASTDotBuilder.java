package de.skuzzle.polly.core.parser.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.ProductType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;


public class ASTDotBuilder extends DotBuilder {

    public ASTDotBuilder(PrintStream out) {
        super(out);
    }
    
    
    
    public void printNameSpace(Namespace ns) {
        this.out.println("subgraph namespace {");
        this.out.println("label=\"Declarations\";");
        this.out.println("style=filled;");
        this.out.println("bgcolor=lightgrey;");
        this.out.println("rank=sink;");
        this.out.println("rankdir=LR;");
        this.out.println("concentrate=true;");
        this.out.println("node [style=filled, shape=Mrecord, bgcolor=white];");
        this.out.print("structNS [fontname=\"Consolas\", label=\"");
        int level = 0;
        for (Namespace space = ns; space != null; space = space.getParent()) {
            this.out.print("Level: ");
            this.out.print(level++);
            this.out.print("|");
            
            for (final List<Declaration> decls : space.getDeclarations().values()) {
                for (final Declaration decl : decls) {
                    Integer i = this.nodes.get(decl);
                    if (i != null) {
                        continue;
                    }
                    i = this.nodeIdx++;
                    this.nodes.put(decl, i);
                    this.out.print("{<n");
                    this.out.print(i);
                    this.out.print("> ");
                    this.out.print(this.escape(decl.getName().getId()));
                    this.out.print("|");
                    this.out.print(this.escape(decl.getType().getName().getId()));
                    this.out.print("}|");
                }
            }
        }
        this.out.println("\"];");
        this.out.println("}");
    }
    
    
    
    private String escape(String s) {
        s = s.replace("\\", "\\\\");
        s = s.replace("|", "\\|");
        s = s.replace("<", "\\<");
        s = s.replace(">", "\\>");
        s = s.replace("{", "\\{");
        s = s.replace("}", "\\}");
        s = s.replace("[", "\\[");
        s = s.replace("]", "\\]");
        return s;
    }
    
    
    
    public void printUsage(VarAccess start, Declaration target) {
        final Integer startIdx = this.nodes.get(start);
        final Integer targetIdx = this.nodes.get(target);
        
        if (startIdx == null) {
            return;
        } else if (targetIdx == null) {
            return;
        }
        
        this.out.print("n");
        this.out.print(startIdx);
        this.out.print("--");
        this.out.print("n");
        this.out.print(targetIdx);
        this.out.print(" [fontname=\"Consolas\", constraint=false,dir=forward, arrowHead=\"open\", label=\"");
        this.out.print("");
        this.out.print("\", style=\"");
        this.out.print("dotted");
        this.out.println("\"];");
    }
    
    
    
    public void printFunction(FunctionLiteral fun) {
        final StringBuilder typesBuilder = new StringBuilder();
        final List<Type> types = new ArrayList<Type>(fun.getFormal().size());
        for (final Declaration decl : fun.getFormal()) {
            types.add(decl.getType());
        }
        final Type signature = new ProductType(types);
        
        for (final Type t : fun.getTypes()) {
            typesBuilder.append(t);
            if (fun.hasConstraint(t)) {
                final Substitution constraint = fun.getConstraint(t);
                typesBuilder.append(" ");
                typesBuilder.append(constraint.map().entrySet().toString());
            }
            typesBuilder.append("\\n");
        }
        this.printNode(fun, 
            "Function",
            "Unique: " + fun.getUnique(),
            "Formal: " + signature.getName(),
            "Types:\\n" + typesBuilder.toString(),
            fun.getPosition().toString()); 
    }
    
    
    
    public void printExpression(String label, Expression exp) {
        final StringBuilder typesBuilder = new StringBuilder();
        for (final Type t : exp.getTypes()) {
            typesBuilder.append(t);
            if (exp.hasConstraint(t)) {
                final Substitution constraint = exp.getConstraint(t);
                typesBuilder.append(" ");
                typesBuilder.append(constraint.map().entrySet().toString());
            }
            typesBuilder.append("\\n");
        }
        this.printNode(exp, 
            label,
            "Unique: " + exp.getUnique(), 
            "Types:\\n" + typesBuilder.toString(),
            exp.getPosition().toString());
    }
}
