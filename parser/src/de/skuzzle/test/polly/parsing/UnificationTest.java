package de.skuzzle.test.polly.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import java.util.List;

import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeGraphVisualizer;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.process.KillingProcessWatcher;
import de.skuzzle.polly.process.ProcessExecutor;


public class UnificationTest {

    public static void main(String[] args) throws ASTTraversalException, IOException {
        test1();
    }
    
    
    
    private static void test1() throws ASTTraversalException, IOException {
        final List<Type> types = new ArrayList<Type>();
        types.add(new ListTypeConstructor(Type.newTypeVar("B")));
        
        
        types.add(
            new MapTypeConstructor(
                new ProductTypeConstructor(Type.newTypeVar("B")),
                Type.newTypeVar("A")));
        
        final Type declared = new MapTypeConstructor(new ProductTypeConstructor(types), 
            new ListTypeConstructor(Type.newTypeVar("A")));
        
        
        final List<Type> types2 = new ArrayList<Type>();
        types2.add(new ListTypeConstructor(Type.NUM));
        
        types2.add(new MapTypeConstructor(
            new ProductTypeConstructor(Type.NUM), Type.STRING));
        
        final Type actual = new MapTypeConstructor(new ProductTypeConstructor(types2), 
            Type.newTypeVar());
        
        System.out.println("Declared: " + declared);
        System.out.println("Actual:   " + actual);
        boolean unified = Type.unify(declared, actual);
        System.out.println("Unified:  " + declared);
        System.out.println(unified);
        
        toPdf(declared, "unified");
    }
    
    
    
    private static void toPdf(Type root, String name) throws IOException {
        TypeGraphVisualizer tgv = new TypeGraphVisualizer();
        tgv.visualize(root, new PrintStream(name + ".dot"));
        String dotPath = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
        ProcessExecutor pe = ProcessExecutor.getOsInstance(false);
        pe.setExecuteIn(new File("C:\\Users\\Simon\\Documents\\Java\\polly\\parser"));
        pe.addCommand(dotPath);
        pe.addCommandsFromString("-Tpdf -o " + name + ".pdf");
        pe.addCommand(name + ".dot");
        pe.setProcessWatcher(new KillingProcessWatcher(10000, true));
        pe.start();
    }
}
