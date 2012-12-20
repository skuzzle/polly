package de.skuzzle.test.polly.parsing;

import java.util.ArrayList;

import java.util.List;

import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class UnificationTest {

    public static void main(String[] args) throws ASTTraversalException {
        test1();
    }
    
    
    
    private static void test1() throws ASTTraversalException {
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
            new ProductTypeConstructor(Type.NUM), Type.newTypeVar()));
        
        final Type actual = new MapTypeConstructor(new ProductTypeConstructor(types2), 
            Type.newTypeVar());
        
        System.out.println("Declared: " + declared);
        System.out.println("Actual:   " + actual);
        boolean unified = Type.unify(declared, actual);
        System.out.println("Unified:  " + declared);
        System.out.println(unified);
    }
}
